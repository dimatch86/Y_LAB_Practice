package org.example.monitoringservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "Модель данных для вывода списка действий пользователя")
public class ActionListResponse {

    @Schema(description = "Список действий пользователей")
    private List<ActionResponseDto> actionResponseDtos = new ArrayList<>();
}
