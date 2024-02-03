package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DbUserRepositoryTest extends AbstractTest {

    DbUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();
        userRepository = new DbUserRepository(jdbcUrl, username, password);
    }

    @Test
    void testDatabaseConnection() throws Exception {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.next();
            int result = rs.getInt(1);
            assertEquals(1, result);
        }
    }

    @Test
    void testSaveUser() throws Exception {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        userRepository.saveUser(user);

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM monitoring_service_schema.user u WHERE u.email = ?");
        ) {
            statement.setString(1, "diman@mail.ru");
            ResultSet rs = statement.executeQuery();
            rs.next();
            assertEquals("diman@mail.ru", rs.getString("email"));
        }
    }

    @Test
    void testFindByEmailUser() {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        userRepository.saveUser(user);

        Optional<User> findUser = userRepository.findByEmail("diman@mail.ru");
        assertTrue(findUser.isPresent());
        assertEquals("diman@mail.ru", findUser.get().getEmail());
    }
}
