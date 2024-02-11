package org.example.monitoringservice.dto.request;

import jakarta.validation.constraints.*;
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
public class ReadingDto {
    @NotNull(message = "Значение не должно быть пустым")
    @Min(0)
    @Max(10000)
    @Digits(integer = 100, fraction = 2)
    private Double value;
    @NotBlank(message = "Тип показаний должен быть указан")
    private String type;
}
