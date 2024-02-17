package org.example.monitoringservice.dto.response;

import lombok.*;

import java.util.UUID;
/**
 * Output data model of reading
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadingResponse {

    private Double value;
    private UUID personalAccount;
    private String readingType;
    private String sendingDate;
}
