package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.model.reading.Reading;

import java.util.List;
import java.util.Optional;

/**
 * Interface for accessing and managing readings and reading types.
 */
public interface ReadingRepository {

    /**
     * Saves a new reading to the repository.
     * @param reading the reading to be saved
     */
    void save(Reading reading);
    /**
     * Saves a new reading type to the repository.
     * @param readingType the reading type to be saved
     */
    void saveNewReadingType(ReadingType readingType);
    /**
     * Retrieves a list of actual readings from the repository.
     * @return a list of actual readings
     */
    List<Reading> findActualReadings();

    /**
     * Retrieves a list of readings for a specific month from the repository.
     *
     * @param monthNumber the number of the month for which readings should be retrieved
     * @return a list of readings for the specified month
     */
    List<Reading> findReadingsByMonth(String monthNumber);

    /**
     * Retrieves a list of all readings history from the repository.
     * @return a list of all readings history
     */
    List<Reading> findReadingsHistory();

    /**
     * Retrieves an available reading type by its type from the repository.
     * @param type the type of reading to be retrieved
     * @return an optional containing the available reading type, or empty if not found
     */
    Optional<ReadingType> findAvailableReadingByType(String type);

    /**
     * Retrieves the latest reading for a specific reading type from the repository.
     * @param readingType the type of reading for which the latest reading should be retrieved
     * @return an optional containing the latest reading for the specified type, or empty if not found
     */
    Optional<Reading> getLatestReading(String readingType);

    /**
     * Retrieves a list of available readings from the repository.
     * @return a list of available readings
     */
    List<String> findAvailableReadings();

}
