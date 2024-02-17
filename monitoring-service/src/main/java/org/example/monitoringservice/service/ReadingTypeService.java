package org.example.monitoringservice.service;

import org.example.monitoringservice.model.reading.ReadingType;

public interface ReadingTypeService {

    /**
     * Adds a new reading type.
     * @param readingType the reading type to be added
     */
    void addNewReadingType(ReadingType readingType);
}
