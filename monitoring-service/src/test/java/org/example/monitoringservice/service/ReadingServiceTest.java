package org.example.monitoringservice.service;

import org.example.monitoringservice.exception.custom.NotAvailableReadingException;
import org.example.monitoringservice.exception.custom.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.exception.custom.TooRecentReadingException;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.DbReadingRepository;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReadingServiceTest {

    private final DbReadingRepository readingRepository = mock(DbReadingRepository.class);
    private final ReadingServiceImpl readingService = new ReadingServiceImpl(readingRepository);
    private UUID personalAccount;

    @BeforeEach
    public void setUp() {
        personalAccount = UUID.randomUUID();
    }

    @Test
    void saveReading_whenReadingIsTooRecent_expectExceptionThrown() {

        Reading existingReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        when(readingRepository.getLatestReading(existingReading.getReadingType())).thenReturn(Optional.of(existingReading));

        Reading newReading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        assertThatThrownBy(() -> readingService.send(newReading))
                .isInstanceOf(TooRecentReadingException.class)
                .hasMessage("Показания передаются раз в месяц");
    }

    @Test
    void saveReading_whenReadingWithNotAvailableType_expectExceptionThrown() {

        Reading existingReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        when(readingRepository.getLatestReading(existingReading.getReadingType())).thenReturn(Optional.of(existingReading));
        when(readingRepository.findAvailableReadings()).thenReturn(List.of("ГОРЯЧАЯ ВОДА", "ХОЛОДНАЯ ВОДА", "ОТОПЛЕНИЕ"));

        Reading newReading = new Reading(25.25, personalAccount, "ЭЛЕКТРИЧЕСТВО", Instant.now());

        assertThatThrownBy(() -> readingService.send(newReading))
                .isInstanceOf(NotAvailableReadingException.class)
                .hasMessageContaining("Не поддерживаемый тип показаний. В настоящее время доступны:");
    }

    @Test
    void addNewReadingType_whenAddNotExistingReadingType_thenCallsSaveNewReadingType() {

        ReadingType notExistingReadingType = new ReadingType("ГАЗ");
        when(readingRepository.findAvailableReadingByType(notExistingReadingType.getType()))
                .thenReturn(Optional.empty());
        readingService.addNewReadingType(notExistingReadingType);
        verify(readingRepository, times(1))
                .saveNewReadingType(notExistingReadingType);
    }

    @Test
    void addNewReadingType_whenAddExistingReadingType_expectExceptionThrown() {

        ReadingType existingReadingType = new ReadingType("ГАЗ");
        when(readingRepository.findAvailableReadingByType("ГАЗ"))
                .thenReturn(Optional.of(existingReadingType));

        assertThatThrownBy(() -> readingService.addNewReadingType(existingReadingType))
                .isInstanceOf(ReadingTypeAlreadyExistsException.class)
                .hasMessage("Тип показаний ГАЗ уже существует в базе");
        verify(readingRepository, times(0))
                .saveNewReadingType(any());
    }
}
