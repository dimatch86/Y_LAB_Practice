package org.example.auditstarter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    @Schema(description = "Время ответа", example = "2024-02-17 12:00:20")
    private String date;

    /**
     * Formats the current date and time into a string with the format "yyyy-MM-dd HH:mm:ss".
     * @return the formatted date and time string
     */
    private static String formatDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public String getDate() {
        return date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setDate(String date) {
        this.date = date;
    }
}