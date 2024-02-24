package org.example.monitoringservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * Output data model of readings list
 */
@Data
@Schema(description = "Модель данных для вывода списка показаний")
public class ReadingListResponseDto {

    @Schema(description = "Список выведенных показаний")
    private List<ReadingResponseDto> readingResponseDtoList = new ArrayList<>();
}
