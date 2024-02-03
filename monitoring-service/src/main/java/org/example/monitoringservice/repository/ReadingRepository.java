package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.reading.Reading;

import java.util.List;


public interface ReadingRepository {

    void saveReading(Reading reading);
    void saveNewReadingType(String newReadingType);
    List<Reading> findActualReadings();

    List<Reading> findReadingsByMonth(String monthNumber);

    List<Reading> findReadingsHistory();

}
