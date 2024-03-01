package org.example.monitoringservice.service;

import org.example.monitoringservice.exception.custom.NotAvailableReadingException;
import org.example.monitoringservice.exception.custom.TooRecentReadingException;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.repository.ReadingRepositoryImpl;
import org.example.monitoringservice.repository.ReadingTypeRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadingServiceTest {

    private final ReadingRepositoryImpl readingRepository = mock(ReadingRepositoryImpl.class);
    private final ReadingTypeRepositoryImpl readingTypeRepository = mock(ReadingTypeRepositoryImpl.class);
    private final ReadingServiceImpl readingService = new ReadingServiceImpl(readingRepository, readingTypeRepository);
    private UUID personalAccount;

    @BeforeEach
    public void setUp() {
        personalAccount = UUID.randomUUID();
    }

    @Test
    void saveReading_whenReadingIsTooRecent_expectExceptionThrown() {

        Reading existingReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        when(readingRepository.getLatestReading(existingReading.getReadingType(), String.valueOf(personalAccount))).thenReturn(Optional.of(existingReading));

        Reading newReading = new Reading(25.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());

        assertThatThrownBy(() -> readingService.send(newReading))
                .isInstanceOf(TooRecentReadingException.class)
                .hasMessage("Показания передаются раз в месяц");
    }

    @Test
    void saveReading_whenReadingWithNotAvailableType_expectExceptionThrown() {

        Reading existingReading = new Reading(12.25, personalAccount, "ГОРЯЧАЯ ВОДА", Instant.now());
        when(readingRepository.getLatestReading(existingReading.getReadingType(), String.valueOf(personalAccount))).thenReturn(Optional.of(existingReading));
        when(readingTypeRepository.findAvailableReadings()).thenReturn(List.of("ГОРЯЧАЯ ВОДА", "ХОЛОДНАЯ ВОДА", "ОТОПЛЕНИЕ"));

        Reading newReading = new Reading(25.25, personalAccount, "ЭЛЕКТРИЧЕСТВО", Instant.now());

        assertThatThrownBy(() -> readingService.send(newReading))
                .isInstanceOf(NotAvailableReadingException.class)
                .hasMessageContaining("Не поддерживаемый тип показаний. В настоящее время доступны:");
    }
}
