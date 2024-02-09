package org.example.monitoringservice.dto.response;

import lombok.*;
import org.example.monitoringservice.model.reading.Reading;

import java.util.List;

/**
 * Data model for output message
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String message;

    private List<Reading> data;
    private String dataString;

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, String dataString) {
        this.message = message;
        this.dataString = dataString;
    }

    public Response(String message, List<Reading> data) {
        this.message = message;
        this.data = data;
    }
}
