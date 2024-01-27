package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.reading.Reading;


public interface ReadingRepository {

    void saveReading(Reading reading);
}
