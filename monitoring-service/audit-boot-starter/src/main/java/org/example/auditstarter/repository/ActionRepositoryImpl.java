package org.example.auditstarter.repository;


import org.example.auditstarter.model.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * This class provides an implementation of the ActionRepository interface
 * for storing and retrieving Action objects using the
 * specified URL, username, and password.
 */
@Repository
public class ActionRepositoryImpl implements ActionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ActionRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
