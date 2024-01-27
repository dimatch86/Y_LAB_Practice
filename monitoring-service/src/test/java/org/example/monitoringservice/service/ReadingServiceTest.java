package org.example.monitoringservice.service;

import org.example.monitoringservice.exception.NotAvailableReadingException;
import org.example.monitoringservice.exception.TooRecentReadingException;
import org.example.monitoringservice.model.reading.MonthEnum;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.InMemoryReadingRepository;
import org.example.monitoringservice.util.AvailableReadingsUtil;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReadingServiceTest {

    private InMemoryReadingRepository readingRepository;
    private ReadingService readingService;
    private UUID personalAccount;
    List<Reading> readings;
    User currentUser;

    @BeforeEach
    public void setUp() {
        readings = new ArrayList<>();
        personalAccount = UUID.randomUUID();
        readingRepository = new InMemoryReadingRepository(readings);
        readingService = new ReadingService(readingRepository);
        currentUser = new User(personalAccount, "user@mail.ru", RoleType.USER, Instant.now());
        UserContext.setCurrentUser(currentUser);
    }

    @Test
    void saveReading_whenReadingIsAdded_expectReadingAdded() {

        Reading existingReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now().minus(40, ChronoUnit.DAYS));
        readingService.send(existingReading);

        Reading newReading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        readingService.send(newReading);

        assertTrue(readingRepository.findAll().contains(newReading));
        assertEquals(2, readingRepository.findAll().size());
    }

    @Test
    void saveReading_whenReadingIsTooRecent_expectExceptionThrown() {

        Reading existingReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        readingService.send(existingReading);

        Reading newReading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        TooRecentReadingException exception = assertThrows(TooRecentReadingException.class, () -> readingService.send(newReading));
        assertEquals("Показания передаются раз в месяц!", exception.getMessage());
    }

    @Test
    void saveReading_whenReadingWithNotAvailableType_expectExceptionThrown() {

        Reading existingReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        readingRepository.saveReading(existingReading);

        Reading newReading = new Reading(25.25, personalAccount, "ЭЛЕКТРИЧЕСТВО", Instant.now());

        NotAvailableReadingException exception = assertThrows(NotAvailableReadingException.class, () -> readingRepository.saveReading(newReading));
        assertEquals("Не поддерживаемый тип показаний", exception.getMessage());
        assertEquals(1, readingRepository.findAll().size());
    }

    @Test
    void addNewReadingType_whenAddNewReadingType_thenNewReadingTypeIsAvailable() {

        String newReadingType = "ГАЗ";
        assertFalse(AvailableReadingsUtil.getAvailableReadings().contains(newReadingType));

        readingService.addNewReadingType(newReadingType);
        assertTrue(AvailableReadingsUtil.getAvailableReadings().contains(newReadingType));
    }

    @Test
    void actualReading_whenCallActualReadings_thenReturnActualReading() {
        Reading oldHotWaterReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now().minus(40, ChronoUnit.DAYS));
        Reading oldColdWaterReading = new Reading(12.25, personalAccount, "ХОЛОДНАЯ ВОДА", Instant.now().minus(40, ChronoUnit.DAYS));

        readings.add(oldColdWaterReading);
        readings.add(oldHotWaterReading);

        Reading newHotWaterReading = new Reading(15.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading newColdWaterReading = new Reading(15.25, personalAccount, "ХОЛОДНАЯ ВОДА", Instant.now());

        readings.add(newColdWaterReading);
        readings.add(newHotWaterReading);

        List<Reading> actualReadings = readingService.getActualReadings();

        assertTrue(actualReadings.contains(newColdWaterReading));
        assertFalse(actualReadings.contains(oldColdWaterReading));
        assertTrue(actualReadings.contains(newHotWaterReading));
        assertFalse(actualReadings.contains(oldHotWaterReading));

        assertEquals(2, actualReadings.size());
    }

    @Test
    void actualReading_whenUserCallsActualReadings_thenReturnHisOwnActualReading() {

        Reading usersReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading someOneReading = new Reading(12.25, UUID.randomUUID(), "ХОЛОДНАЯ ВОДА", Instant.now());
        readings.add(usersReading);
        readings.add(someOneReading);
        List<Reading> actualReadings = readingService.getActualReadings();

        assertTrue(actualReadings.contains(usersReading));
        assertFalse(actualReadings.contains(someOneReading));

        assertEquals(1, readingService.getActualReadings().size());
    }
    @Test
    void actualReading_whenAdminCallsActualReadings_thenReturnAllUsersActualReading() {

        currentUser.setRole(RoleType.ADMIN);

        Reading usersReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        Reading someOnesReading = new Reading(12.25, UUID.randomUUID(), "ХОЛОДНАЯ ВОДА", Instant.now());
        readings.add(usersReading);
        readings.add(someOnesReading);
        List<Reading> actualReadings = readingService.getActualReadings();

        assertTrue(actualReadings.contains(usersReading));
        assertTrue(actualReadings.contains(someOnesReading));

        assertEquals(2, readingService.getActualReadings().size());
    }

    @Test
    void historyOfReadings_whenAdminCallsHistoryOfReadings_thenReturnsAllUsersHistoryReadings() {

        currentUser.setRole(RoleType.ADMIN);

        Reading oldestHotWaterReading = new Reading(12.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", Instant.now().minus(40, ChronoUnit.DAYS));
        Reading oldColdWaterReading = new Reading(12.25, UUID.randomUUID(), "ХОЛОДНАЯ ВОДА", Instant.now().minus(35, ChronoUnit.DAYS));
        Reading newHotWaterReading = new Reading(15.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", Instant.now().minus(5, ChronoUnit.DAYS));
        Reading newestHeatingReading = new Reading(15.25, UUID.randomUUID(), "ОТОПЛЕНИЕ", Instant.now());
        readings.add(oldColdWaterReading);
        readings.add(oldestHotWaterReading);
        readings.add(newestHeatingReading);
        readings.add(newHotWaterReading);

        List<Reading> result = readingService.getHistoryOfReadings();

        assertEquals(4, result.size());
        assertEquals(newestHeatingReading, result.get(0));
        assertEquals(newHotWaterReading, result.get(1));
        assertEquals(oldColdWaterReading, result.get(2));
        assertEquals(oldestHotWaterReading, result.get(3));
    }

    @Test
    void historyOfReadings_whenUserCallsHistoryOfReadings_thenReturnsHisOwnHistoryReadings() {

        Reading oldestHotWaterReading = new Reading(12.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", Instant.now().minus(40, ChronoUnit.DAYS));
        Reading oldColdWaterReading = new Reading(12.25, personalAccount, "ХОЛОДНАЯ ВОДА", Instant.now().minus(35, ChronoUnit.DAYS));
        Reading newHotWaterReading = new Reading(15.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", Instant.now().minus(5, ChronoUnit.DAYS));
        Reading newestHeatingReading = new Reading(15.25, personalAccount, "ОТОПЛЕНИЕ", Instant.now());
        readings.add(oldColdWaterReading);
        readings.add(oldestHotWaterReading);
        readings.add(newestHeatingReading);
        readings.add(newHotWaterReading);

        List<Reading> result = readingService.getHistoryOfReadings();

        assertEquals(2, result.size());
        assertEquals(newestHeatingReading, result.get(0));
        assertEquals(oldColdWaterReading, result.get(1));
    }

    @Test
    void readingsByMonth_whenAdminCallsReadingsByMonth_thenReturnsAllUsersReadings() {

        currentUser.setRole(RoleType.ADMIN);
        Instant dateWithMonth1 = Instant.now();
        Instant dateWithMonth2 = Instant.now().minus(30, ChronoUnit.DAYS);

        Reading hotWaterReadingMonth1 = new Reading(12.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", dateWithMonth1);
        Reading coldWaterReadingMonth1 = new Reading(12.25, UUID.randomUUID(), "ХОЛОДНАЯ ВОДА", dateWithMonth1);
        Reading hotWaterReadingMonth2 = new Reading(15.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", dateWithMonth2);
        Reading heatingReadingMonth2 = new Reading(15.25, UUID.randomUUID(), "ОТОПЛЕНИЕ", dateWithMonth2);
        readings.add(heatingReadingMonth2);
        readings.add(hotWaterReadingMonth1);
        readings.add(coldWaterReadingMonth1);
        readings.add(hotWaterReadingMonth2);

        String monthOfDateWithMonth1 = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault()).getMonth().toString();

        String m = MonthEnum.valueOf(monthOfDateWithMonth1).getMonth();

        List<Reading> result = readingService.getReadingsByMonth(m);

        assertEquals(2, result.size());
        assertFalse(result.contains(heatingReadingMonth2));
        assertFalse(result.contains(hotWaterReadingMonth2));
        assertEquals(hotWaterReadingMonth1, result.get(0));
        assertEquals(coldWaterReadingMonth1, result.get(1));
    }

    @Test
    void readingsByMonth_whenUserCallsReadingsByMonth_thenReturnsHisOwnReadings() {

        Instant dateWithMonth1 = Instant.now();
        Instant dateWithMonth2 = Instant.now().minus(30, ChronoUnit.DAYS);

        Reading hotWaterReadingMonth1 = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", dateWithMonth1);
        Reading coldWaterReadingMonth1 = new Reading(12.25, UUID.randomUUID(), "ХОЛОДНАЯ ВОДА", dateWithMonth1);
        Reading hotWaterReadingMonth2 = new Reading(15.25, UUID.randomUUID(), "ГОРЯЧАЯ ВОДА", dateWithMonth2);
        Reading heatingReadingMonth2 = new Reading(15.25, personalAccount, "ОТОПЛЕНИЕ", dateWithMonth2);
        readings.add(heatingReadingMonth2);
        readings.add(hotWaterReadingMonth1);
        readings.add(coldWaterReadingMonth1);
        readings.add(hotWaterReadingMonth2);

        String monthOfDateWithMonth1 = LocalDateTime.ofInstant(dateWithMonth1, ZoneId.systemDefault()).getMonth().toString();
        String monthNameCyrillic = MonthEnum.valueOf(monthOfDateWithMonth1).getMonth();

        List<Reading> result = readingService.getReadingsByMonth(monthNameCyrillic);

        assertEquals(1, result.size());
        assertFalse(result.contains(heatingReadingMonth2));
        assertFalse(result.contains(hotWaterReadingMonth2));
        assertFalse(result.contains(heatingReadingMonth2));
        assertEquals(hotWaterReadingMonth1, result.get(0));
    }
}
