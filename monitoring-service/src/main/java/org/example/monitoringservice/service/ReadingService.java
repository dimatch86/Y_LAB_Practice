package org.example.monitoringservice.service;

import org.example.monitoringservice.model.reading.Reading;

import java.util.List;
import java.util.UUID;

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
    List<Reading> getActualReadings(String personalAccount);

    /**
     * Retrieves the history of readings.
     * @return a list of reading history
     */
    List<Reading> getHistoryOfReadings(String personalAccount);

    /**
     * Retrieves readings for a specific month.
     * @param month the month for which to retrieve readings
     * @return a list of readings for the specified month
     */
    List<Reading> getReadingsByMonth(int month, String personalAccount);
}
