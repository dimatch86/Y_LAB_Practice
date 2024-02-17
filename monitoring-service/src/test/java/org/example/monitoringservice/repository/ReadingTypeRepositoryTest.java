package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadingTypeRepositoryTest extends AbstractTest {

    @Test
    void addNewReadingType_whenAddNewReadingType_thenNewReadingTypeIsAvailable() {

        UUID personalAccount = UUID.randomUUID();

        User user = new User(personalAccount, "diman@mail.ru", BCrypt.hashpw("userPassword", BCrypt.gensalt()), RoleType.USER, Instant.now());

        UserContext.setCurrentUser(user);

        List<String> availableList = readingRepository.findAvailableReadings();

        ReadingType newReadingType = new ReadingType("ГАЗ");
        assertFalse(availableList.contains("ГАЗ"));

        readingTypeRepository.saveNewReadingType(newReadingType);

        List<String> updatedAvailableList = readingRepository.findAvailableReadings();
        assertTrue(updatedAvailableList.contains("ГАЗ"));
    }
}
