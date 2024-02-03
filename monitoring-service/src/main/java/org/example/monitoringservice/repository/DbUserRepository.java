package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.DbException;
import org.example.monitoringservice.exception.UserAlreadyExistException;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;

import java.sql.*;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DbUserRepository implements UserRepository {
    private final String url;
    private final String userName;
    private final String userPassword;
    @Override
    public void saveUser(User user) {
        String sql = "INSERT INTO monitoring_service_schema.user " +
                "(id, email, password, role_id, personal_account, registration_date) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, userPassword);
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)){
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setInt(3, getRoleIdByRole(user.getRole().toString()));
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
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));

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
                            RoleType.valueOf(getRoleByRoleId(resultSet.getInt("role_id"))),
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

    @Override
    public String getAuthorityInfo(String roleType) {
        String authority = "";
        String sql = "SELECT description FROM monitoring_service_schema.role r " +
                "WHERE r.role_type = ?";
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setString(1, roleType);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    authority = resultSet.getString("description");
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return authority;
    }

    private String getRoleByRoleId(int roleId) {
        String role = "";
        String sql = "SELECT role_type FROM monitoring_service_schema.role r " +
                "WHERE r.id = ?";
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setInt(1, roleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    role = resultSet.getString("role_type");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return role;
    }

    private int getRoleIdByRole(String role) {
        int roleId = 0;
        String sql = "SELECT id FROM monitoring_service_schema.role r" +
                " WHERE r.role_type = ?";
        try (PreparedStatement statement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
            statement.setString(1, role);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    roleId = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return roleId;
    }

}