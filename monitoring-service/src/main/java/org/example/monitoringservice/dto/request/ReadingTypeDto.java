package org.example.monitoringservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model for entering reading type
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingTypeDto {

    @NotBlank(message = "Тип показаний должен быть указан")
    private String type;
}
