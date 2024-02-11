package org.example.monitoringservice.util;

import lombok.experimental.UtilityClass;
import org.example.monitoringservice.dto.response.ResponseEntity;
/**
 * Utility class for constructing response objects.
 */
@UtilityClass
public class ResponseUtil {

    /**
     * Constructs a response entity with data and a success status.
     * @param <T> the type of data
     * @param t the data to be included in the response
     * @return a response entity with the provided data
     */
    public <T> ResponseEntity<T> okResponseWithData(T t) {
        return ResponseEntity.<T>builder()
                .data(t).build();
    }

    /**
     * Constructs a response entity with a success message.
     * @param okMessage the success message
     * @return a response entity with the success message
     */
    public ResponseEntity<Object> okResponse(String okMessage) {
        return ResponseEntity.builder()
                .data(okMessage).build();
    }

    /**
     * Constructs a response entity with an error message.
     * @param errorMessage the error message
     * @return a response entity with the error message
     */
    public ResponseEntity<Object> errorResponse(String errorMessage) {
        return ResponseEntity.builder()
                .error(errorMessage)
                .build();
    }
}
