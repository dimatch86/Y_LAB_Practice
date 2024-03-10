package org.example.auditstarter.repository;


import lombok.RequiredArgsConstructor;
import org.example.auditstarter.model.Action;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * This class provides an implementation of the ActionRepository interface
 * for storing and retrieving Action objects using the
 * specified URL, username, and password.
 */
@Repository
@RequiredArgsConstructor
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
}
