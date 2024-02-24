package org.example.monitoringservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;
/**
 * Output data model of reading
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель данных для вывода сохраненных показаний")
public class ReadingResponseDto {

    @Schema(description = "Значение показаний", example = "45.25")
    private Double value;
    @Schema(description = "Лицевой счет пользователя")
    private UUID personalAccount;
    @Schema(description = "Тип показаний", example = "ЭЛЕКТРИЧЕСТВО")
    private String readingType;
    @Schema(description = "Дата передачи показаний")
    private String sendingDate;
}
