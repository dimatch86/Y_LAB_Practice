package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.aop.Auditable;
import org.example.monitoringservice.exception.custom.NotAvailableReadingException;
import org.example.monitoringservice.exception.custom.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.exception.custom.TooRecentReadingException;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.repository.ReadingRepository;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
/**
 * Implementation of the Reading Service interface.
 */
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository readingRepository;

    /**
     * Method to send readings.
     * @param reading the reading object
     * @throws TooRecentReadingException if the readings are too recent
     * @throws NotAvailableReadingException if the reading type is not available
     */
    @Override
    @Auditable
    public void send(Reading reading) {
        Optional<Reading> latestReading = readingRepository.getLatestReading(reading.getReadingType());
        latestReading.ifPresentOrElse(latest -> {
                    if (isReadingTooRecent(reading, latest)) {
                        throw new TooRecentReadingException("Показания передаются раз в месяц");
                    }
                    readingRepository.save(reading);
                },
                () -> {
                    if (!isAvailableReading(reading)) {
                        throw new NotAvailableReadingException(MessageFormat
                                .format("Не поддерживаемый тип показаний. В настоящее время доступны: {0}",
                                        readingRepository.findAvailableReadings()));
                    }
                    readingRepository.save(reading);
                });
    }

    /**
     * Method to add a new reading type.
     * @param readingType the reading type to be added
     * @throws ReadingTypeAlreadyExistsException if the reading type already exists
     */
    @Override
    @Auditable
    public void addNewReadingType(ReadingType readingType) {
        Optional<ReadingType> availableReading =
                readingRepository.findAvailableReadingByType(readingType.getType());
        if (availableReading.isPresent()) {
            throw new ReadingTypeAlreadyExistsException(MessageFormat
                    .format("Тип показаний {0} уже существует в базе", readingType.getType()));
        }
        readingRepository.saveNewReadingType(readingType);
    }

    /**
     * Returns the list of actual readings.
     * @return a list of actual readings
     */
    @Override
    @Auditable
    public List<Reading> getActualReadings() {
        return readingRepository.findActualReadings();
    }

    /**
     * Returns the list of readings for a specific month.
     * @param month the month for which readings are to be retrieved
     * @return a list of readings for the specified month
     */
    @Override
    @Auditable
    public List<Reading> getReadingsByMonth(String month) {
        return readingRepository.findReadingsByMonth(month);
    }

    /**
     * Returns the history of all readings.
     * @return a list of all readings in the history
     */
    @Override
    @Auditable
    public List<Reading> getHistoryOfReadings() {
        return readingRepository.findReadingsHistory();
    }

    /**
     * Checks if the reading is too recent compared to the latest reading.
     * @param reading the reading to be checked
     * @param latest the latest reading
     * @return true if the reading is too recent, false otherwise
     */
    private boolean isReadingTooRecent(Reading reading, Reading latest) {
        return reading.getSendingDate().isBefore(latest.getSendingDate().plus(30, ChronoUnit.DAYS));
    }

    /**
     * Checks if the reading is available.
     * @param reading the reading to be checked
     * @return true if the reading is available, false otherwise
     */
    private boolean isAvailableReading(Reading reading) {
        List<String> availableReadings = readingRepository.findAvailableReadings();
        return availableReadings.contains(reading.getReadingType());
    }
}
