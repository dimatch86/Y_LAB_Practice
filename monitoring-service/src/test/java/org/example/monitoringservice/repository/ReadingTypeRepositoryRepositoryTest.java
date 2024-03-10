package org.example.monitoringservice.repository;

import org.example.monitoringservice.exception.custom.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.in.controller.AbstractTest;
import org.example.monitoringservice.model.reading.ReadingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Репозиторий типов показаний")
class ReadingTypeRepositoryRepositoryTest extends AbstractTest {

    @Test
    @DisplayName("Добавление нового типа показаний -> новый тип показаний становится доступным")
    void addNewReadingType_whenAddNewReadingType_thenNewReadingTypeIsAvailable() {

        List<String> availableList = readingTypeRepository.findAvailableReadings();

        ReadingType newReadingType = new ReadingType("ГАЗ");
        assertFalse(availableList.contains("ГАЗ"));

        readingTypeRepository.saveNewReadingType(newReadingType);

        List<String> updatedAvailableList = readingTypeRepository.findAvailableReadings();
        assertTrue(updatedAvailableList.contains("ГАЗ"));
    }

    @Test
    @DisplayName("Добавление существующего типа показаний -> исключение")
    void addNewReadingType_whenAddExistingReadingType_expectExceptionThrown() {

        List<String> availableList = readingTypeRepository.findAvailableReadings();

        ReadingType existingReadingType = new ReadingType("ГОРЯЧАЯ ВОДА");
        assertTrue(availableList.contains("ГОРЯЧАЯ ВОДА"));

        assertThatThrownBy(() -> readingTypeRepository.saveNewReadingType(existingReadingType))
                .isInstanceOf(ReadingTypeAlreadyExistsException.class);
    }
}
