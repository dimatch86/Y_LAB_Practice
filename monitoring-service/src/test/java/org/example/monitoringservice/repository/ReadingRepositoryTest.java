package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReadingRepositoryTest extends AbstractTest {


    @Test
    void testSaveReading() throws Exception {

        UUID personalAccount = UUID.randomUUID();

        Reading newReading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        UserContext.setCurrentUser(user);

        readingRepository.save(newReading);

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);

             PreparedStatement statement = conn.prepareStatement("SELECT * FROM monitoring_service_schema.reading r WHERE r.personal_account = ?")
        ) {
            statement.setString(1, String.valueOf(personalAccount));
            ResultSet rs = statement.executeQuery();
            rs.next();
            assertThat(rs.getDouble("reading_value")).isEqualTo(25.25);
        }
    }

    @Test
    void actualReading_whenCallActualReadings_thenReturnActualReading() {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        userRepository.saveUser(user);

        UserContext.setCurrentUser(user);

        Reading oldHotWaterReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА",
                Instant.now().minus(40, ChronoUnit.DAYS));
        Reading oldColdWaterReading = new Reading(13.25, personalAccount, "ХОЛОДНАЯ ВОДА",
                Instant.now().minus(40, ChronoUnit.DAYS));

        readingRepository.save(oldColdWaterReading);
        readingRepository.save(oldHotWaterReading);

        Reading newHotWaterReading = new Reading(15.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading newColdWaterReading = new Reading(16.25, personalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());

        readingRepository.save(newColdWaterReading);
        readingRepository.save(newHotWaterReading);

        List<Reading> actualReadings = readingRepository.findActualReadings();
        assertThat(actualReadings).hasSize(2);
    }

    @Test
    void actualReading_whenUserCallsActualReadings_thenReturnHisOwnActualReading() {

        UUID personalAccount = UUID.randomUUID();
        UUID otherPersonalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        User otherUser = new User(otherPersonalAccount, "ivan@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        userRepository.saveUser(user);
        userRepository.saveUser(otherUser);

        Reading usersReading = new Reading(125.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading someOneReading = new Reading(121.25, otherPersonalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());


        UserContext.setCurrentUser(user);
        readingRepository.save(usersReading);

        UserContext.setCurrentUser(otherUser);
        readingRepository.save(someOneReading);

        UserContext.setCurrentUser(user);

        List<Reading> actualReadings = readingRepository.findActualReadings();

        assertThat(actualReadings).hasSize(1);
        assertThat(personalAccount).isEqualTo(actualReadings.get(0).getPersonalAccount());
    }

    @Test
    void actualReading_whenAdminCallsActualReadings_thenReturnAllUsersActualReading() {

        UUID userPersonalAccount = UUID.randomUUID();
        UUID adminPersonalAccount = UUID.randomUUID();

        User user = new User(userPersonalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        User admin = new User(adminPersonalAccount, "ivan@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.ADMIN, Instant.now());

        userRepository.saveUser(user);
        userRepository.saveUser(admin);

        Reading usersReading = new Reading(125.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading adminReading = new Reading(121.25, adminPersonalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());


        UserContext.setCurrentUser(user);
        readingRepository.save(usersReading);

        UserContext.setCurrentUser(admin);
        readingRepository.save(adminReading);

        List<Reading> actualReadings = readingRepository.findActualReadings();
        List<UUID> accounts = actualReadings.stream().map(Reading::getPersonalAccount).toList();

        assertThat(actualReadings).hasSize(2);
        assertThat(accounts).containsAll(List.of(userPersonalAccount, adminPersonalAccount));
    }

    @Test
    void readingsByMonth_whenUserCallsReadingsByMonth_thenReturnsHisOwnReadings() {

        UUID userPersonalAccount = UUID.randomUUID();
        UUID adminPersonalAccount = UUID.randomUUID();

        User user = new User(userPersonalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        User admin = new User(adminPersonalAccount, "ivan@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.ADMIN, Instant.now());

        userRepository.saveUser(user);
        userRepository.saveUser(admin);

        Instant dateWithMonth1 = Instant.now();
        Instant dateWithMonth2 = Instant.now().minus(30, ChronoUnit.DAYS);

        Reading hotWaterReadingMonth1 = new Reading(12.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth1);
        Reading coldWaterReadingMonth1 = new Reading(12.25, adminPersonalAccount, "ХОЛОДНАЯ ВОДА", dateWithMonth1);
        Reading hotWaterReadingMonth2 = new Reading(15.25, adminPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth2);
        Reading heatingReadingMonth2 = new Reading(15.25, userPersonalAccount, "ОТОПЛЕНИЕ", dateWithMonth2);

        UserContext.setCurrentUser(user);
        readingRepository.save(hotWaterReadingMonth1);
        readingRepository.save(heatingReadingMonth2);

        UserContext.setCurrentUser(admin);
        readingRepository.save(coldWaterReadingMonth1);
        readingRepository.save(hotWaterReadingMonth2);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault());
        int month = localDateTime.getMonthValue();

        UserContext.setCurrentUser(user);
        List<Reading> result = readingRepository.findReadingsByMonth(month);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPersonalAccount()).isEqualTo(hotWaterReadingMonth1.getPersonalAccount());
    }

    @Test
    void readingsByMonth_whenAdminCallsReadingsByMonth_thenReturnsAllUsersReadings() {

        UUID userPersonalAccount = UUID.randomUUID();
        UUID adminPersonalAccount = UUID.randomUUID();

        User user = new User(userPersonalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());
        User admin = new User(adminPersonalAccount, "ivan@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.ADMIN, Instant.now());

        userRepository.saveUser(user);
        userRepository.saveUser(admin);

        Instant dateWithMonth1 = Instant.now();
        Instant dateWithMonth2 = Instant.now().minus(30, ChronoUnit.DAYS);

        Reading hotWaterReadingMonth1 = new Reading(12.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth1);
        Reading coldWaterReadingMonth1 = new Reading(12.25, adminPersonalAccount, "ХОЛОДНАЯ ВОДА", dateWithMonth1);
        Reading hotWaterReadingMonth2 = new Reading(15.25, adminPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth2);
        Reading heatingReadingMonth2 = new Reading(15.25, userPersonalAccount, "ОТОПЛЕНИЕ", dateWithMonth2);

        UserContext.setCurrentUser(user);
        readingRepository.save(hotWaterReadingMonth1);
        readingRepository.save(heatingReadingMonth2);

        UserContext.setCurrentUser(admin);
        readingRepository.save(coldWaterReadingMonth1);
        readingRepository.save(hotWaterReadingMonth2);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault());
        int month = localDateTime.getMonthValue();

        UserContext.setCurrentUser(admin);
        List<Reading> result = readingRepository.findReadingsByMonth(month);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPersonalAccount()).isEqualTo(userPersonalAccount);
        assertThat(result.get(1).getPersonalAccount()).isEqualTo(adminPersonalAccount);
    }
}
