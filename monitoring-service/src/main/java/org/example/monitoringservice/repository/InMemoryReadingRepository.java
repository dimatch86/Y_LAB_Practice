package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.NotAvailableReadingException;
import org.example.monitoringservice.exception.TooRecentReadingException;
import org.example.monitoringservice.model.reading.MonthEnum;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.util.AvailableReadingsUtil;
import org.example.monitoringservice.util.UserContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InMemoryReadingRepository implements ReadingRepository {

    private final List<Reading> readings;

    @Override
    public void saveReading(Reading reading) {
        readings.stream()
                .filter(existedReading -> existedReading.getPersonalAccount().equals(reading.getPersonalAccount()))
                .filter(existedReading -> existedReading.getReadingType().equals(reading.getReadingType()))
                .max(Comparator.comparing(Reading::getSendingDate))
                .ifPresentOrElse(latestReading -> {
                    if (reading.getSendingDate().isAfter(latestReading.getSendingDate().plus(30, ChronoUnit.DAYS))) {
                        readings.add(reading);
                    } else {
                        throw new TooRecentReadingException("Показания передаются раз в месяц!");
                    }
                },
                        () -> {
                    if (AvailableReadingsUtil.getAvailableReadings().contains(reading.getReadingType())) {
                            readings.add(reading);
                    } else {
                        throw new NotAvailableReadingException("Не поддерживаемый тип показаний");
                    }
                        });
    }

    public List<Reading> findActualReadingsForCurrentUser() {

        Map<String, Reading> latestReadingsByType = readings.stream()
                .filter(reading -> reading.getPersonalAccount().equals(UserContext.getCurrentUser().getPersonalAccount()))
                .sorted(Comparator.comparing(Reading::getSendingDate).reversed())
                .collect(Collectors.toMap(Reading::getReadingType,
                        Function.identity(),
                        (existing, replacement) -> existing.getSendingDate().isAfter(replacement.getSendingDate())
                                ? existing
                                : replacement));

        return latestReadingsByType.values().stream().toList();
    }

    public List<Reading> findActualReadingsForAllUsers() {

        Map<UUID, Map<String, Reading>> latestReadingsByAccountAndType = readings.stream()
                .sorted(Comparator.comparing(Reading::getSendingDate).reversed())
                .collect(Collectors.groupingBy(Reading::getPersonalAccount,
                        Collectors.toMap(Reading::getReadingType,
                                Function.identity(),
                                (existing, replacement) -> existing.getSendingDate().isAfter(replacement.getSendingDate()) // В случае конфликта выбираем объект с наибольшей датой
                                        ? existing
                                        : replacement)));

        return latestReadingsByAccountAndType.values().stream().toList()
                .stream()
                .flatMap(map -> map.values().stream()).toList();
    }

    public List<Reading> findReadingsHistory() {
        return readings.stream()
                .filter(reading -> !UserContext.getCurrentUser().getRole().equals(RoleType.USER) ||
                        reading.getPersonalAccount().equals(UserContext.getCurrentUser().getPersonalAccount()))
                .sorted(Comparator.comparing(Reading::getSendingDate).reversed())
                .toList();
    }

    public List<Reading> findReadingsByMonth(String month) {
        return readings.stream()
                .filter(reading -> !UserContext.getCurrentUser().getRole().equals(RoleType.USER) ||
                        reading.getPersonalAccount().equals(UserContext.getCurrentUser().getPersonalAccount()))
                .filter(reading ->
                        compareMonth(reading.getSendingDate(), month))
                .toList();
    }

    public List<Reading> findAll() {
        return readings;
    }

    private MonthEnum getEnumByMonth(String month) {
        return Arrays.stream(MonthEnum.values())
                .filter(monthEnum -> monthEnum.getMonth().equals(month))
                .findFirst()
                .orElse(MonthEnum.EMPTY);
    }
    private boolean compareMonth(Instant instant, String month) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                .getMonth().toString()
                .equals(getEnumByMonth(month).toString());
    }
}
