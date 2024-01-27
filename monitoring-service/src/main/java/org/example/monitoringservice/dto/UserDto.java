package org.example.monitoringservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data model for entering user details
 */
@Data
@AllArgsConstructor
public class UserDto {
    private String email;
    private String role;
}
