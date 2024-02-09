package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.model.reading.Reading;

import java.util.List;
import java.util.Optional;


public interface ReadingRepository {

    void saveReading(Reading reading);
    void saveNewReadingType(ReadingType readingType);
    List<Reading> findActualReadings();

    List<Reading> findReadingsByMonth(String monthNumber);

    List<Reading> findReadingsHistory();
    Optional<ReadingType> findAvailableReadingByType(String type);

}
