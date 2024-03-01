package org.example.auditstarter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Модель данных для вывода списка действий пользователя")
public class ActionListResponseDto {

    @Schema(description = "Список действий пользователей")
    private List<ActionResponseDto> actionResponseDtos = new ArrayList<>();

    public List<ActionResponseDto> getActionResponseDtos() {
        return actionResponseDtos;
    }

    public void setActionResponseDtos(List<ActionResponseDto> actionResponseDtos) {
        this.actionResponseDtos = actionResponseDtos;
    }
}
