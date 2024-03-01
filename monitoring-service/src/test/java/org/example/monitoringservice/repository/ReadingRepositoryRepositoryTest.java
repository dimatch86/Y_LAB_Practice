package org.example.monitoringservice.repository;

import org.example.monitoringservice.in.controller.AbstractTest;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий показаний")
class ReadingRepositoryRepositoryTest extends AbstractTest {

    private UUID adminPersonalAccount;
    private UUID userPersonalAccount;


    @BeforeEach
    public void setUsers() {
        adminPersonalAccount = UUID.randomUUID();
        userPersonalAccount = UUID.randomUUID();
        User user = new User(userPersonalAccount, "user1@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.ROLE_USER, Instant.now());
        User admin = new User(adminPersonalAccount, "admin1@mail.ru", BCrypt.hashpw("adminPassword", BCrypt.gensalt()), RoleType.ROLE_ADMIN, Instant.now());
        userRepository.saveUser(user);
        userRepository.saveUser(admin);
    }


    @Test
    @DisplayName("Тест сохранения показаний")
    void testSaveReading() {

        UUID personalAccount = UUID.randomUUID();

        Reading newReading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        readingRepository.save(newReading);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM monitoring_service_schema.reading r WHERE r.personal_account = ?", String.valueOf(personalAccount));
        rowSet.next();
        assertThat(rowSet.getDouble("reading_value")).isEqualTo(25.25);

    }

    @Test
    @DisplayName("Запрос актуальных показаний -> получение последних показаний")
    void actualReading_whenCallActualReadings_thenReturnActualReading() {

        Reading oldHotWaterReading = new Reading(12.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА",
                Instant.now().minus(40, ChronoUnit.DAYS));
        Reading oldColdWaterReading = new Reading(13.25, userPersonalAccount, "ХОЛОДНАЯ ВОДА",
                Instant.now().minus(40, ChronoUnit.DAYS));

        readingRepository.save(oldColdWaterReading);
        readingRepository.save(oldHotWaterReading);

        Reading newHotWaterReading = new Reading(15.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading newColdWaterReading = new Reading(16.25, userPersonalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());

        readingRepository.save(newColdWaterReading);
        readingRepository.save(newHotWaterReading);

        List<Reading> actualReadings = readingRepository.findActualReadings(String.valueOf(userPersonalAccount));
        assertThat(actualReadings).hasSize(2);
    }

    @Test
    @DisplayName("Запрос актуальных показаний с ролью USER -> получение только своих показаний")
    void actualReading_whenUserCallsActualReadings_thenReturnHisOwnActualReading() {

        Reading usersReading = new Reading(125.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading someOneReading = new Reading(121.25, adminPersonalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());


        readingRepository.save(usersReading);
        readingRepository.save(someOneReading);

        List<Reading> actualReadings = readingRepository.findActualReadings(String.valueOf(userPersonalAccount));

        assertThat(actualReadings).hasSize(1);
        assertThat(userPersonalAccount).isEqualTo(actualReadings.get(0).getPersonalAccount());
    }

    @Test
    @DisplayName("Запрос актуальных показаний с ролью ADMIN -> получение показаний всех пользователей")
    void actualReading_whenAdminCallsActualReadings_thenReturnAllUsersActualReading() {


        Reading usersReading = new Reading(125.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading adminReading = new Reading(121.25, adminPersonalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());

        readingRepository.save(usersReading);

        readingRepository.save(adminReading);

        List<Reading> actualReadings = readingRepository.findActualReadings(String.valueOf(adminPersonalAccount));
        List<UUID> accounts = actualReadings.stream().map(Reading::getPersonalAccount).toList();

        assertThat(actualReadings).hasSize(2);
        assertThat(accounts).containsAll(List.of(userPersonalAccount, adminPersonalAccount));
    }

    @Test
    @DisplayName("Запрос показаний за месяц с ролью USER -> получение только своих показаний")
    void readingsByMonth_whenUserCallsReadingsByMonth_thenReturnsHisOwnReadings() {

        Instant dateWithMonth1 = Instant.now();
        Instant dateWithMonth2 = Instant.now().minus(30, ChronoUnit.DAYS);

        Reading hotWaterReadingMonth1 = new Reading(12.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth1);
        Reading coldWaterReadingMonth1 = new Reading(12.25, adminPersonalAccount, "ХОЛОДНАЯ ВОДА", dateWithMonth1);
        Reading hotWaterReadingMonth2 = new Reading(15.25, adminPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth2);
        Reading heatingReadingMonth2 = new Reading(15.25, userPersonalAccount, "ОТОПЛЕНИЕ", dateWithMonth2);

        readingRepository.save(hotWaterReadingMonth1);
        readingRepository.save(heatingReadingMonth2);

        readingRepository.save(coldWaterReadingMonth1);
        readingRepository.save(hotWaterReadingMonth2);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault());
        int month = localDateTime.getMonthValue();

        List<Reading> result = readingRepository.findReadingsByMonth(month, String.valueOf(userPersonalAccount));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPersonalAccount()).isEqualTo(hotWaterReadingMonth1.getPersonalAccount());
    }

    @Test
    @DisplayName("Запрос показаний за месяц с ролью ADMIN -> получение показаний всех пользователей")
    void readingsByMonth_whenAdminCallsReadingsByMonth_thenReturnsAllUsersReadings() {

        Instant dateWithMonth1 = Instant.now();
        Instant dateWithMonth2 = Instant.now().minus(30, ChronoUnit.DAYS);

        Reading hotWaterReadingMonth1 = new Reading(12.25, userPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth1);
        Reading coldWaterReadingMonth1 = new Reading(12.25, adminPersonalAccount, "ХОЛОДНАЯ ВОДА", dateWithMonth1);
        Reading hotWaterReadingMonth2 = new Reading(15.25, adminPersonalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth2);
        Reading heatingReadingMonth2 = new Reading(15.25, userPersonalAccount, "ОТОПЛЕНИЕ", dateWithMonth2);

        readingRepository.save(hotWaterReadingMonth1);
        readingRepository.save(heatingReadingMonth2);

        readingRepository.save(coldWaterReadingMonth1);
        readingRepository.save(hotWaterReadingMonth2);

        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault());
        int month = localDateTime.getMonthValue();

        List<Reading> result = readingRepository.findReadingsByMonth(month, String.valueOf(adminPersonalAccount));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPersonalAccount()).isEqualTo(userPersonalAccount);
        assertThat(result.get(1).getPersonalAccount()).isEqualTo(adminPersonalAccount);
    }
}
