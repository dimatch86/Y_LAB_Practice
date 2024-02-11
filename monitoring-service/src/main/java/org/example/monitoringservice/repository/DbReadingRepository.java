package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.custom.DbException;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.util.UserContext;

import java.sql.*;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Implementation of the ReadingRepository interface that interacts with a database.
 */
@RequiredArgsConstructor
public class DbReadingRepository implements ReadingRepository {

    /**
     * The URL for accessing the repository.
     */
    private final String url;
    /**
     * The username for accessing the repository.
     */
    private final String userName;
    /**
     * The password for accessing the repository.
     */
    private final String userPassword;

    /**
     * Saves a new reading to the database.
     * @param reading the reading to be saved
     * @throws SQLException if a database access error occurs
     */
    public void save(Reading reading) {

        String sql = "INSERT INTO monitoring_service_schema.reading " +
                "(id, reading_value, personal_account, type_id, sending_date) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, userPassword);
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
                preparedStatement.setDouble(1, reading.getValue());
                preparedStatement.setString(2, String.valueOf(reading.getPersonalAccount()));
                preparedStatement.setInt(3, getTypeIdByType(reading.getReadingType()));
                preparedStatement.setTimestamp(4, java.sql.Timestamp.from(reading.getSendingDate()));

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            }

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * Retrieves the latest reading for a specific reading type from the database.
     * @param readingType the type of reading for which the latest reading should be retrieved
     * @return an optional containing the latest reading for the specified type, or empty if not found
     * @throws SQLException if a database access error occurs
     */
    public Optional<Reading> getLatestReading(String readingType) {
        Optional<Reading> latestReading = Optional.empty();
        String latestReadingQuery = "SELECT * " +
                "FROM monitoring_service_schema.reading r " +
                "JOIN monitoring_service_schema.available_reading ar ON r.type_id = ar.id " +
                "WHERE r.type_id = " +
                "(SELECT id FROM monitoring_service_schema.available_reading arr WHERE arr.type = ?) " +
                "AND r.personal_account = ? " +
                "ORDER BY r.sending_date DESC LIMIT 1";
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(latestReadingQuery)) {
            statement.setString(1, readingType);
            statement.setString(2, UserContext.getCurrentUser().getPersonalAccount().toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    latestReading = Optional.of(new Reading(
                            resultSet.getDouble("reading_value"),
                            UUID.fromString(resultSet.getString("personal_account")),
                            getTypeByTypeId(resultSet.getInt("type_id")),
                            resultSet.getDate("sending_date").toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return latestReading;
    }

    /**
     * Saves a new reading type to the database.
     * @param readingType The reading type to be saved
     * @throws DbException if an error occurs while saving the reading type to the database
     */
    @Override
    public void saveNewReadingType(ReadingType readingType) {
        String sql = "INSERT INTO monitoring_service_schema.available_reading " +
                "(id, type) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?)";
        try (PreparedStatement preparedStatement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)){
            preparedStatement.setString(1, readingType.getType());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
    }

    /**
     * Finds the actual readings for the users based on their roles.
     * @return A list of Reading objects representing the actual readings found
     */
    @Override
    public List<Reading> findActualReadings() {

        String sql = "SELECT * FROM monitoring_service_schema.reading r JOIN" +
                "    ( SELECT type_id, MAX(sending_date) AS max_date" +
                "     FROM monitoring_service_schema.reading GROUP BY type_id, personal_account) max_dates" +
                "     ON r.type_id = max_dates.type_id AND r.sending_date = max_dates.max_date" +
                "     JOIN monitoring_service_schema.user u ON r.personal_account = u.personal_account" +
                "     WHERE  (SELECT u.role" +
                "            from monitoring_service_schema.user u" +
                "            where u.personal_account = ?) = 'ADMIN'" +
                "     OR (SELECT u.role from monitoring_service_schema.user u" +
                "      where u.personal_account = ?) = 'USER' AND u.personal_account = ?";

        List<Reading> readings = new ArrayList<>();
        String personalAccount = String.valueOf(UserContext.getCurrentUser().getPersonalAccount());
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {

            statement.setString(1, personalAccount);
            statement.setString(2, personalAccount);
            statement.setString(3, personalAccount);

            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    Reading reading = new Reading(
                            resultSet.getDouble("reading_value"),
                            UUID.fromString(resultSet.getString("personal_account")),
                            getTypeByTypeId(resultSet.getInt("type_id")),
                            resultSet.getDate("sending_date").toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
                    readings.add(reading);
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return readings;
    }


    /**
     * Finds the readings for a specific month based on the month number and user role.
     * @param monthNumber The month number to filter the readings
     * @return A list of Reading objects representing the readings found for the specified month
     */
    @Override
    public List<Reading> findReadingsByMonth(String monthNumber) {
        String sql ="SELECT * FROM monitoring_service_schema.reading r" +
                "    JOIN monitoring_service_schema.available_reading ar ON r.type_id = ar.id" +
                "    JOIN monitoring_service_schema.user u ON r.personal_account = u.personal_account" +
                "    WHERE  extract(MONTH FROM sending_date) = ? and (SELECT u.role" +
                "            from monitoring_service_schema.user u" +
                "                                          where u.personal_account = ?) = 'ADMIN'" +
                "OR extract(MONTH FROM sending_date) = ? and (SELECT u.role from monitoring_service_schema.user u" +
                "     where u.personal_account = ?) = 'USER' AND u.personal_account = ? " +
                "order by sending_date desc";
        List<Reading> readings = new ArrayList<>();
        int month = Integer.parseInt(monthNumber);
        String personalAccount = String.valueOf(UserContext.getCurrentUser().getPersonalAccount());
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {

            statement.setInt(1, month);
            statement.setString(2, personalAccount);
            statement.setInt(3, month);
            statement.setString(4, personalAccount);
            statement.setString(5, personalAccount);

            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    Reading reading = new Reading(
                            resultSet.getDouble("reading_value"),
                            UUID.fromString(resultSet.getString("personal_account")),
                            getTypeByTypeId(resultSet.getInt("type_id")),
                            resultSet.getDate("sending_date").toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
                    readings.add(reading);
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return readings;
    }

    /**
     * Finds the readings history based on the user role.
     * @return A list of Reading objects representing the readings history
     */
    @Override
    public List<Reading> findReadingsHistory() {
        String sql = "SELECT * FROM monitoring_service_schema.reading r " +
                "     JOIN monitoring_service_schema.user u ON r.personal_account = u.personal_account " +
                "     WHERE  (SELECT u.role" +
                "            from monitoring_service_schema.user u" +
                "            where u.personal_account = ?) = 'ADMIN'" +
                "     OR (SELECT u.role from monitoring_service_schema.user u" +
                "      where u.personal_account = ?) = 'USER' AND u.personal_account = ? " +
                "     ORDER BY sending_date DESC ";

        List<Reading> readings = new ArrayList<>();
        String personalAccount = String.valueOf(UserContext.getCurrentUser().getPersonalAccount());
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {

            statement.setString(1, personalAccount);
            statement.setString(2, personalAccount);
            statement.setString(3, personalAccount);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    Reading reading = new Reading(
                            resultSet.getDouble("reading_value"),
                            UUID.fromString(resultSet.getString("personal_account")),
                            getTypeByTypeId(resultSet.getInt("type_id")),
                            resultSet.getDate("sending_date").toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
                    readings.add(reading);
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return readings;
    }

    /**
     * Finds the available readings from the database.
     * @return A list of strings representing the available readings
     */
    public List<String> findAvailableReadings() {
        String sql ="SELECT * FROM monitoring_service_schema.available_reading";
        List<String> availableReadings = new ArrayList<>();
        try (Statement statement = DriverManager.getConnection(url, userName, userPassword).createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while(resultSet.next()) {
                    availableReadings.add(resultSet.getString("type"));
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return availableReadings;
    }

    /**
     * Finds the available reading by type from the database.
     * @param type The type of the reading to find
     * @return An Optional object containing the ReadingType if found
     */
    public Optional<ReadingType> findAvailableReadingByType(String type) {
        String sql ="SELECT * FROM monitoring_service_schema.available_reading ar " +
                "WHERE ar.type = ?";
        Optional<ReadingType> availableReading = Optional.empty();
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setString(1, type);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    availableReading = Optional.of(new ReadingType(
                            resultSet.getString("type")));

                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return availableReading;
    }

    /**
     * Retrieves the type based on the type ID.
     * @param typeId The ID of the type to find
     * @return A String representing the type
     */
    private String getTypeByTypeId(int typeId) {
        String type = "";
        String sql = "SELECT ar.type FROM monitoring_service_schema.available_reading ar " +
                "WHERE ar.id = ?";
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setInt(1, typeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    type = resultSet.getString("type");
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return type;
    }

    /**
     * Retrieves the type ID based on the given type.
     * @param type The type of the reading
     * @return An integer representing the type ID
     */
    private int getTypeIdByType(String type) {
        int typeId = 0;
        String sql = "SELECT id FROM monitoring_service_schema.available_reading ar" +
                " WHERE ar.type = ?";
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setString(1, type);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    typeId = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return typeId;
}}
