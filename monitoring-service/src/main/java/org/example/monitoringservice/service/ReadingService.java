package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.repository.InMemoryReadingRepository;
import org.example.monitoringservice.util.AvailableReadingsUtil;
import org.example.monitoringservice.util.UserContext;

import java.util.List;

@RequiredArgsConstructor
public class ReadingService {

    private final InMemoryReadingRepository repository;

    public void send(Reading reading) {
        repository.saveReading(reading);
    }

    public void addNewReadingType(String newReadingType) {
        AvailableReadingsUtil.addNewReadingType(newReadingType);
    }

    public List<Reading> getActualReadings() {
        if (UserContext.getCurrentUser().getRole().equals(RoleType.ADMIN)) {
            return repository.findActualReadingsForAllUsers();
        } else {
            return repository.findActualReadingsForCurrentUser();
        }
    }

    public List<Reading> getReadingsByMonth(String month) {
        return repository.findReadingsByMonth(month);
    }

    public List<Reading> getHistoryOfReadings() {
        return repository.findReadingsHistory();
    }
}
