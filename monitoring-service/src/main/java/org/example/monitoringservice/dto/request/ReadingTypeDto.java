package org.example.monitoringservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model for entering reading type
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных для добавления нового типа показаний")
public class ReadingTypeDto {

    @NotNull
    @NotBlank(message = "Тип показаний должен быть указан")
    @Schema(description = "Тип показаний", example = "ГАЗ")
    private String type;
}
