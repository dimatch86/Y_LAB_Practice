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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

class DbUserRepositoryTest extends AbstractTest {

    DbUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new DbUserRepository(jdbcUrl, username, password);
    }

    @Test
    void testDatabaseConnection() throws Exception {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.next();
            int result = rs.getInt(1);
            assertThat(result).isEqualTo(1);
        }
    }

    @Test
    void testSaveUser() throws Exception {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        userRepository.saveUser(user);

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM monitoring_service_schema.user u WHERE u.email = ?");
        ) {
            statement.setString(1, "diman@mail.ru");
            ResultSet rs = statement.executeQuery();
            rs.next();
            assertThat(rs.getString("email")).isEqualTo("diman@mail.ru");
        }
    }

    @Test
    void testFindByEmailUser() {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        userRepository.saveUser(user);

        Optional<User> findUser = userRepository.findByEmail("diman@mail.ru");
        assertTrue(findUser.isPresent());
        assertThat(findUser.get().getEmail()).isEqualTo("diman@mail.ru");
    }
}
