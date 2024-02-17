package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.reading.ReadingType;

import java.util.Optional;

/**
 * Interface for accessing and managing reading types.
 */
public interface ReadingTypeRepository {

    /**
     * Saves a new reading type to the repository.
     * @param readingType the reading type to be saved
     */
    void saveNewReadingType(ReadingType readingType);
    /**
     * Retrieves an available reading type by its type from the repository.
     * @param type the type of reading to be retrieved
     * @return an optional containing the available reading type, or empty if not found
     */
    Optional<ReadingType> findAvailableReadingByType(String type);
}
