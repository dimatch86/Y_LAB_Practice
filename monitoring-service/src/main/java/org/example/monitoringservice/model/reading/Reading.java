package org.example.monitoringservice.model.reading;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;
import java.util.UUID;
/**
 * This class represents a reading, including the reading value,
 * the personal account, the type of reading, and the sending date.
 */
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class Reading {

    /**
     * The value of the reading.
     */
    private Double readingValue;

    /**
     * The personal account UUID associated with the reading.
     */
    private UUID personalAccount;

    /**
     * The type of reading (e.g., electricity, water, gas).
     */
    private String readingType;

    /**
     * The date when the reading was sent. Defaults to the current date and time.
     */
    @Builder.Default
    private Instant sendingDate = Instant.now();
}
