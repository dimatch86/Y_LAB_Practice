package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.repository.ReadingRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository readingRepository;

    @Override
    public void send(Reading reading) {
        readingRepository.saveReading(reading);
    }

    @Override
    public void addNewReadingType(ReadingType readingType) {
        Optional<ReadingType> availableReading =
                readingRepository.findAvailableReadingByType(readingType.getType());
        if (availableReading.isPresent()) {
            throw new ReadingTypeAlreadyExistsException(MessageFormat
                    .format("Тип показаний {0} уже существует в базе", readingType.getType()));
        }
        readingRepository.saveNewReadingType(readingType);
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
