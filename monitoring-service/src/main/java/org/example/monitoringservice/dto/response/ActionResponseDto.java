package org.example.monitoringservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных для представления действия пользователя")
public class ActionResponseDto {
    @Schema(description = "Выполненное действие пользователя")

    private String actionMethod;

    /**
     * The user who performed the action. Defaults to the current user's email in UserContext.
     */
    @Schema(description = "Кем выполнено действие")
    private String actionedBy;

    /**
     * The time when the action was created. Defaults to the current time.
     */
    @Schema(description = "Дата выполнения действия")
    private String createAt;
}
