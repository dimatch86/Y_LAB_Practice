package org.example.monitoringservice.service;

import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.reading.ReadingType;

import java.util.List;

public interface ReadingService {
    void send(Reading reading);
    List<Reading> getActualReadings();
    List<Reading> getHistoryOfReadings();
    List<Reading> getReadingsByMonth(String month);
    void addNewReadingType(ReadingType readingType);
}
