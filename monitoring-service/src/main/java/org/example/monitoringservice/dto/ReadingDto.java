package org.example.monitoringservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Data model for entering readings
 */
@Data
@Builder
public class ReadingDto {
    private double value;
    private UUID personalAccount;
    private String type;
}
