package org.example.monitoringservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Обобщенная модель данных для ответа")
public class ResponseDto<T> {

    @Schema(description = "Сообщение, если отсутствуют данные в ответе", example = "Запрос выполнен успешно")
    private String message;
    /**
     * The data contained in the response
     */
    @Schema(description = "Данные ответа")
    private T data;

    /**
     * The error message, if any
     */
    @Schema(description = "Сообщение в случае ошибки", example = "some error has been occurred")
    private String error;
    /**
     * The date and time of the response
     */
    @Builder.Default
    @Schema(description = "Время ответа", example = "2024-02-17 12:00:20")
    private String date = formatDate();

    /**
     * Formats the current date and time into a string with the format "yyyy-MM-dd HH:mm:ss".
     * @return the formatted date and time string
     */
    private static String formatDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}