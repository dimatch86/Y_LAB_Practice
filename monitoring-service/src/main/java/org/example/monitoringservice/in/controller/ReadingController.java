package org.example.monitoringservice.in.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.exception.custom.ParameterMissingException;
import org.example.monitoringservice.in.controller.swagger.SwaggerReadingController;
import org.example.monitoringservice.mapper.mapstruct.ReadingMapper;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.service.ReadingService;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for processing commands with meter readings
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/reading")
public class ReadingController implements SwaggerReadingController {

    private final ReadingService readingService;
    private final ReadingMapper readingMapper;

    /**
     * Endpoint for sending reading data.
     * @param readingDto the reading data to be sent
     * @return a ResponseEntity with a success message
     */
    @PostMapping( "/send")
    @Override
    public ResponseEntity<ResponseDto<?>> sendReading(@RequestBody @Valid ReadingDto readingDto) {
        if (UserContext.isNotAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        readingService.send(readingMapper.readingDtoToReading(readingDto));
        return ResponseEntity.ok(ResponseUtil.okResponse("Показания успешно отправлены"));
    }

    /**
     * Endpoint for retrieving actual readings.
     * @return a ResponseEntity containing the actual readings
     */
    @GetMapping("/actual")
    @Override
    public ResponseEntity<ResponseDto<List<ReadingResponse>>> getActualReadings() {
        if (UserContext.isNotAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(readingMapper
                .readingListToResponseList(readingService.getActualReadings())));
    }

    /**
     * Endpoint for retrieving the history of readings.
     * @return a ResponseEntity containing the history of readings
     */
    @GetMapping("/history")
    @Override
    public ResponseEntity<ResponseDto<List<ReadingResponse>>> getHistoryOfReadings() {
        if (UserContext.isNotAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(readingMapper
                .readingListToResponseList(readingService.getHistoryOfReadings())));
    }

    /**
     * Endpoint for retrieving readings by month.
     * @param monthNumber the number of the month for which readings are to be retrieved
     * @return a ResponseEntity containing the readings for the specified month
     */
    @GetMapping("/month")
    @Override
    public ResponseEntity<ResponseDto<List<ReadingResponse>>> getReadingsByMonth(@RequestParam(value = "monthNumber", required = false) Integer monthNumber) {
        if (UserContext.isNotAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        if (monthNumber == null) {
            throw new ParameterMissingException("Не задан номер месяца");
        }
        List<Reading> readingsByMonth = readingService.getReadingsByMonth(monthNumber);
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(readingMapper
                .readingListToResponseList(readingsByMonth)));
    }
}