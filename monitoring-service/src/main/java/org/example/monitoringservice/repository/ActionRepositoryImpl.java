package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.model.audit.Action;
import org.example.monitoringservice.util.CaseConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an implementation of the ActionRepository interface
 * for storing and retrieving Action objects using the
 * specified URL, username, and password.
 */
@RequiredArgsConstructor
@Repository
public class ActionRepositoryImpl implements ActionRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Saves the given Action object to the repository by inserting into the database.
     * @param action The Action object to be saved.
     */
    @Override
    public void save(Action action) {
        String sql = "INSERT INTO monitoring_service_schema.action " +
                "(id, action_method, actioned_by, create_at) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?, ?, ?)";
        jdbcTemplate.update(sql, action.getActionMethod(),
                action.getActionedBy(), java.sql.Timestamp.from(action.getCreateAt()));
    }

    /**
     * Retrieves all the Action objects stored in the repository from the database.
     * @return A list of all the Action objects.
     */
    @Override
    public List<Action> findAllActions() {
        String sql = "SELECT * FROM monitoring_service_schema.action a " +
                "     ORDER BY a.create_at DESC ";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        List<Action> actions = new ArrayList<>();
        while (rowSet.next()) {
            Action action = new Action(
                    rowSet.getString(CaseConverter.camelCaseToSnakeCase(Action.Fields.actionMethod)),
                    rowSet.getString(CaseConverter.camelCaseToSnakeCase(Action.Fields.actionedBy)), rowSet.getDate(CaseConverter.camelCaseToSnakeCase(Action.Fields.createAt))
                            .toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            actions.add(action);
        }
        return actions;
    }
}
