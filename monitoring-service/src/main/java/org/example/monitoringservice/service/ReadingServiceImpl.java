package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.repository.ReadingRepository;

import java.util.List;

@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository readingRepository;

    @Override
    public void send(Reading reading) {
        readingRepository.saveReading(reading);
    }

    @Override
    public void addNewReadingType(String newReadingType) {
        readingRepository.saveNewReadingType(newReadingType);
    }

    @Override
    public List<Reading> getActualReadings() {
        return readingRepository.findActualReadings();
    }

    @Override
    public List<Reading> getReadingsByMonth(String month) {
        return readingRepository.findReadingsByMonth(month);
    }

    @Override
    public List<Reading> getHistoryOfReadings() {
        return readingRepository.findReadingsHistory();
    }
}
