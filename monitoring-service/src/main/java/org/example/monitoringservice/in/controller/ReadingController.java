package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.ReadingDto;
import org.example.monitoringservice.mapper.ReadingMapper;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.response.Response;
import org.example.monitoringservice.service.ReadingService;
import org.example.monitoringservice.util.UserContext;

import java.text.MessageFormat;
import java.util.List;

/**
 * Controller for processing commands with meter readings
 */
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;
    private final ReadingMapper readingMapper = new ReadingMapper();
    public Response sendReading(ReadingDto readingDto) {
        readingService.send(readingMapper.readingDtoToReading(readingDto));
        System.out.println("Ваши показания сохранены");
        return new Response(MessageFormat.format("Пользователь {0} отправил показания",
                UserContext.getCurrentUser().getEmail()));
    }

    public Response getActualReadings() {
        List<Reading> actualReadings = readingService.getActualReadings();
        actualReadings.forEach(System.out::println);
        return new Response(MessageFormat.format("Пользователь {0} запросил актуальные показания",
                UserContext.getCurrentUser().getEmail()));
    }

    public Response getHistoryOfReadings() {
        List<Reading> history = readingService.getHistoryOfReadings();
        history.forEach(System.out::println);
        return new Response(MessageFormat.format("Пользователь {0} запросил историю показаний",
                UserContext.getCurrentUser().getEmail()));
    }

    public Response getReadingsByMonth(String month) {
        List<Reading> readingsByMonth = readingService.getReadingsByMonth(month);
        readingsByMonth.forEach(System.out::println);
        return new Response(MessageFormat.format("Пользователь {0} запросил показания за {1}",
                UserContext.getCurrentUser().getEmail(), month));
    }

    public Response addNewReadingType(String newReadingType) {
        readingService.addNewReadingType(newReadingType);
        System.out.println("Новый тип показаний добавлен");
        return new Response(MessageFormat.format("Пользователь {0} добавил новый тип показаний {1}",
                UserContext.getCurrentUser().getEmail(), newReadingType));
    }
}
