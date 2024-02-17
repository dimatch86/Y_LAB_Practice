package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.custom.DbException;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;

import java.sql.*;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
/**
 * Implementation of the UserRepository interface that interacts with a database.
 */
@RequiredArgsConstructor
public class DbUserRepository implements UserRepository {
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
     * This method saves the user information into the database.
     * @param user The User object containing the user information.
     */
    @Override
    public void saveUser(User user) {
        String sql = "INSERT INTO monitoring_service_schema.user " +
                "(id, email, password, role, personal_account, registration_date) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, userPassword);
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)){
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getRole().toString());
                preparedStatement.setString(4, user.getPersonalAccount().toString());
                preparedStatement.setTimestamp(5, java.sql.Timestamp.from(user.getRegistrationDate()));

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
     * This method finds a user by their email in the database.
     * @param userEmail The email of the user to be found.
     * @return An Optional containing the user if found, or an empty Optional if not found.
     */
    @Override
    public Optional<User> findByEmail(String userEmail) {
        Optional<User> user = Optional.empty();
        String sql = "SELECT * FROM monitoring_service_schema.user u WHERE u.email = ?";

        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setString(1, userEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = Optional.of(new User(
                            UUID.fromString(resultSet.getString("personal_account")),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            RoleType.valueOf(resultSet.getString("role")),
                            resultSet.getDate("registration_date").toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant())
                    );
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return user;
    }
}
