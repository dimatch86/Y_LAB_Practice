package org.example.monitoringservice.repository;

import org.example.monitoringservice.in.controller.AbstractTest;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий пользователей")
class UserRepositoryRepositoryTest extends AbstractTest {


    @Test
    @DisplayName("Тест сохранения пользователя")
    void testSaveUser()  {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.ROLE_USER, Instant.now());

        userRepository.saveUser(user);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM monitoring_service_schema.user u WHERE u.email = 'diman@mail.ru'");
        rowSet.next();
        assertThat(rowSet.getString("email")).isEqualTo("diman@mail.ru");
    }

    @Test
    @DisplayName("Тест запроса пользователя по email")
    void testFindByEmailUser() {
        Optional<User> findUser = userRepository.findByEmail("user@mail.ru");
        assertTrue(findUser.isPresent());
        assertThat(findUser.get().getEmail()).isEqualTo("user@mail.ru");
    }
}
