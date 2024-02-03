package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.mapper.ReadingMapper;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.dto.response.Response;
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
        return new Response(MessageFormat.format("Пользователь {0} отправил показания",
                UserContext.getCurrentUser().getEmail()));
    }

    public Response getActualReadings() {
        List<Reading> actualReadings = readingService.getActualReadings();
        return new Response(MessageFormat.format("Пользователь {0} запросил актуальные показания",
                UserContext.getCurrentUser().getEmail()), actualReadings);
    }

    public Response getHistoryOfReadings() {
        List<Reading> history = readingService.getHistoryOfReadings();
        return new Response(MessageFormat.format("Пользователь {0} запросил историю показаний",
                UserContext.getCurrentUser().getEmail()), history);
    }

    public Response getReadingsByMonth(String monthNumber) {
        List<Reading> readingsByMonth = readingService.getReadingsByMonth(monthNumber);
        return new Response(MessageFormat.format("Пользователь {0} запросил показания за {1}",
                UserContext.getCurrentUser().getEmail(), monthNumber), readingsByMonth);
    }

    public Response addNewReadingType(String newReadingType) {
        readingService.addNewReadingType(newReadingType);
        return new Response(MessageFormat.format("Пользователь {0} добавил новый тип показаний {1}",
                UserContext.getCurrentUser().getEmail(), newReadingType));
    }
}