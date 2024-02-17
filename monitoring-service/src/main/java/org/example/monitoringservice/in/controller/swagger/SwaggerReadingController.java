package org.example.monitoringservice.in.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.monitoringservice.dto.request.ReadingDto;
import org.example.monitoringservice.dto.response.ReadingResponse;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Контроллер для работы с показаниями")
public interface SwaggerReadingController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Показания успешно переданы"),
            @ApiResponse(responseCode = "404", description = "Не поддерживаемый тип показаний",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content)})
    @Operation(summary = "Передача показаний")
    ResponseEntity<ResponseDto<?>> sendReading(@RequestBody @Valid ReadingDto readingDto);

    @Operation(summary = "Вывод списка актуальных показаний. Пользователь с ролью \"USER\" получает список только своих покаказаний")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content)})
    ResponseEntity<ResponseDto<List<ReadingResponse>>> getActualReadings();

    @Operation(summary = "Вывод истории показаний. Пользователь с ролью \"USER\" получает список только своих покаказаний")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content)})
    ResponseEntity<ResponseDto<List<ReadingResponse>>> getHistoryOfReadings();

    @Operation(summary = "Вывод показаний за определенный месяц. Номер месяца указывается в параметре запроса. " +
            "Пользователь с ролью \"USER\" получает список только своих покаказаний")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос, отсутствует параметр",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content)})
    ResponseEntity<ResponseDto<List<ReadingResponse>>> getReadingsByMonth(@RequestParam(value = "monthNumber", required = false)
                                                                          @Parameter(name = "monthNumber", description = "month number parameter", example = "2") Integer monthNumber);
}
