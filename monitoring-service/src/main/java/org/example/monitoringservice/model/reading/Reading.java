package org.example.monitoringservice.model.reading;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Reading {

    private double value;
    private UUID personalAccount;
    private String readingType;
    @Builder.Default
    private Instant sendingDate = Instant.now();
}
