package org.example.monitoringservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Builder.Default
    private UUID personalAccount = UUID.randomUUID();
    private String email;
    private RoleType role;
    @Builder.Default
    private Instant registrationDate = Instant.now();
}
