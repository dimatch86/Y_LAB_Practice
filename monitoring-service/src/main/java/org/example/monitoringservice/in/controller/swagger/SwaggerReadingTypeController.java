package org.example.monitoringservice.in.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.monitoringservice.dto.request.ReadingTypeDto;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Контроллер для работы с типами показаний")
public interface SwaggerReadingTypeController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class, type = "array"))),
            @ApiResponse(responseCode = "400", description = "Попытка добаления уже существующего типа", content = @Content),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован или не является администратором",
                    content = @Content)})
    @Operation(summary = "Добавление нового типа показаний")
    ResponseEntity<ResponseDto<?>> addNewReadingType(@RequestBody @Valid ReadingTypeDto readingTypeDto);


}
