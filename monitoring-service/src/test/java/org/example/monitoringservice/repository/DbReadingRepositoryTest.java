package org.example.monitoringservice.repository;

import org.example.monitoringservice.exception.NotAvailableReadingException;
import org.example.monitoringservice.exception.TooRecentReadingException;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DbReadingRepositoryTest extends AbstractTest {



    @Test
    void testSaveReading() throws Exception {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();
        String username = postgreSQLContainer.getUsername();
        String password = postgreSQLContainer.getPassword();
        UUID personalAccount = UUID.randomUUID();

        Reading newReading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        UserContext.setCurrentUser(user);

        readingRepository.saveReading(newReading);

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);

             PreparedStatement statement = conn.prepareStatement("SELECT * FROM monitoring_service_schema.reading r WHERE r.personal_account = ?")
        ) {
            statement.setString(1, String.valueOf(personalAccount));
            ResultSet rs = statement.executeQuery();
            rs.next();
            assertEquals(25.25, rs.getDouble("reading_value"));
        }
    }

    @Test
    void saveReading_whenReadingIsTooRecent_expectExceptionThrown() {

        UUID personalAccount = UUID.randomUUID();

        Reading reading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        UserContext.setCurrentUser(user);

        readingRepository.saveReading(reading);

        Reading newReading = new Reading(44.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        TooRecentReadingException exception = assertThrows(TooRecentReadingException.class, () -> readingRepository.saveReading(newReading));
        assertEquals("Показания передаются раз в месяц!", exception.getMessage());
    }

    @Test
    void saveReading_whenReadingWithNotAvailableType_expectExceptionThrown() {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        UserContext.setCurrentUser(user);

        Reading notAvailableReading = new Reading(25.25, personalAccount, "ЭЛЕКТРИЧЕСТВО", Instant.now());

        NotAvailableReadingException exception = assertThrows(NotAvailableReadingException.class, () -> readingRepository.saveReading(notAvailableReading));
        assertEquals("Не поддерживаемый тип показаний. В настоящее время доступны: [ГОРЯЧАЯ ВОДА, ХОЛОДНАЯ ВОДА, ОТОПЛЕНИЕ]", exception.getMessage());
    }

    @Test
    void addNewReadingType_whenAddNewReadingType_thenNewReadingTypeIsAvailable() {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        UserContext.setCurrentUser(user);

        List<String> availableList = readingRepository.findAvailableReadings();

        String newReadingType = "ГАЗ";
        assertFalse(availableList.contains("ГАЗ"));

        readingRepository.saveNewReadingType(newReadingType);

        List<String> updatedAvailableList = readingRepository.findAvailableReadings();
        assertTrue(updatedAvailableList.contains("ГАЗ"));
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

        readingRepository.saveReading(oldColdWaterReading);
        readingRepository.saveReading(oldHotWaterReading);

        Reading newHotWaterReading = new Reading(15.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading newColdWaterReading = new Reading(16.25, personalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());

        readingRepository.saveReading(newColdWaterReading);
        readingRepository.saveReading(newHotWaterReading);

        List<Reading> actualReadings = readingRepository.findActualReadings();
        assertEquals(2, actualReadings.size());
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
        readingRepository.saveReading(usersReading);

        UserContext.setCurrentUser(otherUser);
        readingRepository.saveReading(someOneReading);

        UserContext.setCurrentUser(user);

        List<Reading> actualReadings = readingRepository.findActualReadings();

        assertEquals(1, actualReadings.size());
        assertEquals(actualReadings.get(0).getPersonalAccount(), personalAccount);
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
        readingRepository.saveReading(usersReading);

        UserContext.setCurrentUser(admin);
        readingRepository.saveReading(adminReading);

        List<Reading> actualReadings = readingRepository.findActualReadings();

        assertEquals(2, actualReadings.size());
        List<UUID> accounts = actualReadings.stream().map(Reading::getPersonalAccount).toList();
        assertTrue(accounts.containsAll(List.of(userPersonalAccount, adminPersonalAccount)));
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
        readingRepository.saveReading(hotWaterReadingMonth1);
        readingRepository.saveReading(heatingReadingMonth2);

        UserContext.setCurrentUser(admin);
        readingRepository.saveReading(coldWaterReadingMonth1);
        readingRepository.saveReading(hotWaterReadingMonth2);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault());
        int month = localDateTime.getMonthValue();

        UserContext.setCurrentUser(user);
        List<Reading> result = readingRepository.findReadingsByMonth(String.valueOf(month));

        assertEquals(1, result.size());
        assertEquals(hotWaterReadingMonth1.getPersonalAccount(), result.get(0).getPersonalAccount());
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
        readingRepository.saveReading(hotWaterReadingMonth1);
        readingRepository.saveReading(heatingReadingMonth2);

        UserContext.setCurrentUser(admin);
        readingRepository.saveReading(coldWaterReadingMonth1);
        readingRepository.saveReading(hotWaterReadingMonth2);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault());
        int month = localDateTime.getMonthValue();

        UserContext.setCurrentUser(admin);
        List<Reading> result = readingRepository.findReadingsByMonth(String.valueOf(month));

        assertEquals(2, result.size());
        assertEquals(result.get(0).getPersonalAccount(), userPersonalAccount);
        assertEquals(result.get(1).getPersonalAccount(), adminPersonalAccount);
    }
}
