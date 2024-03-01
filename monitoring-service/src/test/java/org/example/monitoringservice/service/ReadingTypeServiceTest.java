package org.example.monitoringservice.service;

import org.example.monitoringservice.exception.custom.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.repository.ReadingTypeRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReadingTypeServiceTest {

    private final ReadingTypeRepositoryImpl readingTypeRepository = mock(ReadingTypeRepositoryImpl.class);
    private final ReadingTypeServiceImpl readingService = new ReadingTypeServiceImpl(readingTypeRepository);

    @Test
    void addNewReadingType_whenAddNotExistingReadingType_thenCallsSaveNewReadingType() {

        ReadingType notExistingReadingType = new ReadingType("ГАЗ");
        when(readingTypeRepository.findAvailableReadingByType(notExistingReadingType.getType()))
                .thenReturn(Optional.empty());
        readingService.addNewReadingType(notExistingReadingType);
        verify(readingTypeRepository, times(1))
                .saveNewReadingType(notExistingReadingType);
    }

    @Test
    void addNewReadingType_whenAddExistingReadingType_expectExceptionThrown() {

        ReadingType existingReadingType = new ReadingType("ГАЗ");
        when(readingTypeRepository.findAvailableReadingByType("ГАЗ"))
                .thenReturn(Optional.of(existingReadingType));

        assertThatThrownBy(() -> readingService.addNewReadingType(existingReadingType))
                .isInstanceOf(ReadingTypeAlreadyExistsException.class)
                .hasMessage("Тип показаний ГАЗ уже существует в базе");
        verify(readingTypeRepository, times(0))
                .saveNewReadingType(any());
    }
}
