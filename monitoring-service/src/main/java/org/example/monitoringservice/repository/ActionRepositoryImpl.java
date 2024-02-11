package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.custom.DbException;
import org.example.monitoringservice.model.audit.Action;

import java.sql.*;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
/**
 * This class provides an implementation of the ActionRepository interface
 * for storing and retrieving Action objects using the
 * specified URL, username, and password.
 */
@RequiredArgsConstructor
public class ActionRepositoryImpl implements ActionRepository {

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
     * Saves the given Action object to the repository by inserting into the database.
     * @param action The Action object to be saved.
     */
    @Override
    public void save(Action action) {
        String sql = "INSERT INTO monitoring_service_schema.action " +
                "(id, action_method, actioned_by, create_at) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, userPassword);
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatement = DriverManager.getConnection(url, userName, userPassword).prepareStatement(sql)) {
                preparedStatement.setString(1, action.getActionMethod());
                preparedStatement.setString(2, action.getActionedBy());
                preparedStatement.setTimestamp(3, java.sql.Timestamp.from(action.getCreateAt()));

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
     * Retrieves all the Action objects stored in the repository from the database.
     * @return A list of all the Action objects.
     */
    @Override
    public List<Action> findAllActions() {
        String sql = "SELECT * FROM monitoring_service_schema.action a " +
                "     ORDER BY a.create_at DESC ";

        List<Action> actions = new ArrayList<>();

        try (Statement statement = DriverManager.getConnection(url, userName, userPassword).createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while(resultSet.next()) {
                    Action action = new Action(
                            resultSet.getString("action_method"),
                            resultSet.getString("actioned_by"),
                            resultSet.getDate("create_at").toLocalDate()
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
                    actions.add(action);
                }
            }
        } catch (SQLException e) {
            throw new DbException(MessageFormat
                    .format("Ошибка базы данных: {0}", e.getLocalizedMessage()));
        }
        return actions;
    }
}
