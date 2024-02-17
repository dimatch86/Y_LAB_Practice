package org.example.monitoringservice.service;

import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.reading.ReadingType;

import java.util.List;
/**
 * Interface for handling reading data and types.
 */
public interface ReadingService {

    /**
     * Sends a new reading.
     * @param reading the reading to be sent
     */
    void send(Reading reading);

    /**
     * Retrieves the actual readings.
     * @return a list of actual readings
     */
    List<Reading> getActualReadings();

    /**
     * Retrieves the history of readings.
     * @return a list of reading history
     */
    List<Reading> getHistoryOfReadings();

    /**
     * Retrieves readings for a specific month.
     * @param month the month for which to retrieve readings
     * @return a list of readings for the specified month
     */
    List<Reading> getReadingsByMonth(String month);

    /**
     * Adds a new reading type.
     * @param readingType the reading type to be added
     */
    void addNewReadingType(ReadingType readingType);
}
