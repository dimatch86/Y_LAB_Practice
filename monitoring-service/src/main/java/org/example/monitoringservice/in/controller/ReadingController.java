package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.request.ReadingTypeDto;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.mapper.ReadingMapper;
import org.example.monitoringservice.mapper.ReadingTypeMapper;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.model.reading.ReadingType;
import org.example.monitoringservice.service.ReadingService;
import org.example.monitoringservice.util.ResponseUtil;

import java.util.List;

/**
 * Controller for processing commands with meter readings
 */
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    /**
     * Endpoint for sending reading data.
     * @param readingDto the reading data to be sent
     * @return a ResponseEntity with a success message
     */
    public ResponseEntity<Object> sendReading(ReadingDto readingDto) {
        readingService.send(ReadingMapper.READING_MAPPER.readingDtoToReading(readingDto));
        return ResponseUtil.okResponse("Показания успешно отправлены");
    }

    /**
     * Endpoint for retrieving actual readings.
     * @return a ResponseEntity containing the actual readings
     */
    public ResponseEntity<List<ReadingResponse>> getActualReadings() {
        return ResponseUtil.okResponseWithData(ReadingMapper.READING_MAPPER
                .readingListToResponseLIst(readingService.getActualReadings()));
    }

    /**
     * Endpoint for retrieving the history of readings.
     * @return a ResponseEntity containing the history of readings
     */
    public ResponseEntity<List<ReadingResponse>> getHistoryOfReadings() {
        return ResponseUtil.okResponseWithData(ReadingMapper.READING_MAPPER
                .readingListToResponseLIst(readingService.getHistoryOfReadings()));
    }

    /**
     * Endpoint for retrieving readings by month.
     * @param monthNumber the number of the month for which readings are to be retrieved
     * @return a ResponseEntity containing the readings for the specified month
     */
    public ResponseEntity<List<ReadingResponse>> getReadingsByMonth(String monthNumber) {
        List<Reading> readingsByMonth = readingService.getReadingsByMonth(monthNumber);
        return ResponseUtil.okResponseWithData(ReadingMapper.READING_MAPPER
                .readingListToResponseLIst(readingsByMonth));
    }

    /**
     * Endpoint for adding a new reading type.
     * @param readingTypeDto the information of the reading type to be added
     * @return a ResponseEntity with a success message
     */
    public ResponseEntity<Object> addNewReadingType(ReadingTypeDto readingTypeDto) {
        ReadingType readingType = ReadingTypeMapper.READING_TYPE_MAPPER
                .readingTypeDtoToReadingType(readingTypeDto);
        readingService.addNewReadingType(readingType);
        return ResponseUtil.okResponse("Новый тип показаний сохранен");
    }
}