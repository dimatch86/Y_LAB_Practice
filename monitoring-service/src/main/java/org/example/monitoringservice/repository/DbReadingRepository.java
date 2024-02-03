package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.DbException;
import org.example.monitoringservice.exception.NotAvailableReadingException;
import org.example.monitoringservice.exception.TooRecentReadingException;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.util.UserContext;

import java.sql.*;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DbReadingRepository implements ReadingRepository {

    private final String url;
    private final String userName;
    private final String userPassword;
    @Override
    public void saveReading(Reading reading) {
        Optional<Reading> latestReading = getLatestReading(reading.getReadingType());
        latestReading.ifPresentOrElse(latest -> {
                    try {
                        saveIfReadingNotRecent(reading, latest);
                    } catch (SQLException e) {
                        throw new DbException(MessageFormat
                                .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
                    }
                },
                () -> {
                    try {
                        saveIfReadingIsAvailable(reading);
                    } catch (SQLException e) {
                        throw new DbException(MessageFormat
                                .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
                    }
                });
    }
    private Optional<Reading> getLatestReading(String readingType) {
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
    private void saveIfReadingNotRecent(Reading reading, Reading latest) throws SQLException {
        if (reading.getSendingDate().isAfter(latest.getSendingDate().plus(30, ChronoUnit.DAYS))) {
            save(reading);
        } else {
            throw new TooRecentReadingException("Показания передаются раз в месяц!");
        }
    }

    private void saveIfReadingIsAvailable(Reading reading) throws SQLException {
        if (isExist(reading.getReadingType())) {
            save(reading);
        } else {
            throw new NotAvailableReadingException(MessageFormat
                    .format("Не поддерживаемый тип показаний. В настоящее время доступны: {0}",
                            findAvailableReadings()));
        }
    }
    private boolean isExist(String readingType) {

        String sql = "SELECT EXISTS (SELECT 1 FROM monitoring_service_schema.available_reading ar " +
                "WHERE ar.type = ?)";
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setString(1, readingType);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return false;
    }

    private void save(Reading reading) throws SQLException {

        String sql = "INSERT INTO monitoring_service_schema.reading " +
                "(id, reading_value, personal_account, type_id, sending_date) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, userPassword);
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
                preparedStatement.setDouble(1, reading.getValue());
                preparedStatement.setString(2, String.valueOf(UserContext.getCurrentUser().getPersonalAccount()));
                preparedStatement.setInt(3, getTypeIdByType(reading.getReadingType()));
                preparedStatement.setTimestamp(4, java.sql.Timestamp.from(reading.getSendingDate()));

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void saveNewReadingType(String newReadingType) {
        String sql = "INSERT INTO monitoring_service_schema.available_reading " +
                "(id, type) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?)";
        try (PreparedStatement preparedStatement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)){
            preparedStatement.setString(1, newReadingType);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
    }

    @Override
    public List<Reading> findActualReadings() {

        String sql = "SELECT * FROM monitoring_service_schema.reading r JOIN" +
                "    ( SELECT type_id, MAX(sending_date) AS max_date" +
                "     FROM monitoring_service_schema.reading GROUP BY type_id, personal_account) max_dates" +
                "     ON r.type_id = max_dates.type_id AND r.sending_date = max_dates.max_date" +
                "     JOIN monitoring_service_schema.user u ON r.personal_account = u.personal_account" +
                "     JOIN monitoring_service_schema.role ur ON u.role_id = ur.id" +
                "     WHERE  (SELECT ur.role_type" +
                "            from monitoring_service_schema.role ur" +
                "                                         join monitoring_service_schema.user u ON u.role_id = ur.id where u.personal_account = ?) = 'ADMIN'" +
                "     OR (SELECT ur.role_type from monitoring_service_schema.role ur" +
                "     JOIN monitoring_service_schema.user u ON u.role_id = ur.id where u.personal_account = ?) = 'USER' AND u.personal_account = ?";

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


    @Override
    public List<Reading> findReadingsByMonth(String monthNumber) {
        String sql ="SELECT * FROM monitoring_service_schema.reading r" +
                "    JOIN monitoring_service_schema.available_reading ar ON r.type_id = ar.id" +
                "    JOIN monitoring_service_schema.user u ON r.personal_account = u.personal_account" +
                "    JOIN monitoring_service_schema.role ur ON u.role_id = ur.id" +
                "    WHERE  extract(MONTH FROM sending_date) = ? and (SELECT ur.role_type" +
                "            from monitoring_service_schema.role ur" +
                "                                         join monitoring_service_schema.user u ON u.role_id = ur.id where u.personal_account = ?) = 'ADMIN'" +
                "OR extract(MONTH FROM sending_date) = ? and (SELECT ur.role_type from monitoring_service_schema.role ur" +
                "    join monitoring_service_schema.user u ON u.role_id = ur.id where u.personal_account = ?) = 'USER' AND u.personal_account = ? " +
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

    @Override
    public List<Reading> findReadingsHistory() {
        String sql = "SELECT * FROM monitoring_service_schema.reading r " +
                "     JOIN monitoring_service_schema.user u ON r.personal_account = u.personal_account " +
                "     JOIN monitoring_service_schema.role ur ON u.role_id = ur.id " +
                "     WHERE  (SELECT ur.role_type FROM monitoring_service_schema.role ur " +
                "     JOIN monitoring_service_schema.user u ON u.role_id = ur.id " +
                "     WHERE u.personal_account = ?) = 'ADMIN' " +
                "     OR (SELECT ur.role_type FROM monitoring_service_schema.role ur " +
                "     JOIN monitoring_service_schema.user u ON u.role_id = ur.id " +
                "     WHERE u.personal_account = ?) = 'USER' AND u.personal_account = ? " +
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
