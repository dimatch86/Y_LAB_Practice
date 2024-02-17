package org.example.monitoringservice.in.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.monitoringservice.dto.response.ActionResponseDto;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Контроллер для аудита действий пользователя")
public interface SwaggerAuditController {

    @Operation(summary = "Получение списка совершенных действий пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован или не является администратором",
                    content = @Content)})
    ResponseEntity<ResponseDto<List<ActionResponseDto>>> getUsersActionsList();
}
