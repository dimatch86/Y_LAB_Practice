package org.example.monitoringservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * The ResponseEntity class represents an HTTP response entity containing data or error information.
 * @param <T> the type of the data contained in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEntity<T> {

    /**
     * The data contained in the response
     */
    private T data;

    /**
     * The error message, if any
     */
    private String error;
    /**
     * The date and time of the response
     */
    @Builder.Default
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