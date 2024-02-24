package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.custom.UserAlreadyExistException;
import org.example.monitoringservice.mapper.jdbc_mapper.UserRawMapper;
import org.example.monitoringservice.model.user.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Optional;
/**
 * Implementation of the UserRepository interface that interacts with a database.
 */
@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    /**
     * This method saves the user information into the database.
     * @param user The User object containing the user information.
     */
    @Override
    @Transactional
    public void saveUser(User user) {
        String sql = "INSERT INTO monitoring_service_schema.user " +
                "(id, email, password, role, personal_account, registration_date) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, user.getEmail(), user.getPassword(),
                    user.getRole().toString(), user.getPersonalAccount(),
                    java.sql.Timestamp.from(user.getRegistrationDate()));

        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistException(MessageFormat
                    .format("Пользователь с email {0} уже зарегистрирован", user.getEmail()));
        }
    }

    /**
     * This method finds a user by their email in the database.
     * @param userEmail The email of the user to be found.
     * @return An Optional containing the user if found, or an empty Optional if not found.
     */
    @Override
    public Optional<User> findByEmail(String userEmail) {
        String sql = "SELECT * FROM monitoring_service_schema.user u WHERE u.email = ?";
        return Optional.ofNullable(jdbcTemplate.query(sql, new UserRawMapper(), userEmail));
    }
}
