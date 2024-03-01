package org.example.monitoringservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data model for entering readings
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных для отправки показаний")
public class ReadingDto {

    @Pattern(regexp = "^[0-9]*\\.?[0-9]+$", message = "Некорректный ввод значения. " +
            "Введите целое или дробное число с точкой")
    @NotNull(message = "Значение показаний должно быть указано")
    @Schema(description = "Значение показаний", example = "45.25")
    private String value;
    @NotNull
    @NotBlank(message = "Тип показаний должен быть указан")
    @Schema(description = "Тип показаний", example = "ГОРЯЧАЯ ВОДА")
    private String type;
}
