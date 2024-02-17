package org.example.monitoringservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;
import java.util.UUID;
/**
 * This class represents a user with details such as the personal account UUID, email, password,
 * role, and registration date.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class User {

    /**
     * The UUID of the user's personal account. Defaults to a random UUID.
     */
    @Builder.Default
    private UUID personalAccount = UUID.randomUUID();
    /**
     * The email of the user.
     */
    private String email;
    /**
     * The password of the user.
     */
    private String password;
    /**
     * The role of the user, such as USER or ADMIN.
     */
    private RoleType role;
    /**
     * The date when the user was registered. Defaults to the current date and time.
     */
    @Builder.Default
    private Instant registrationDate = Instant.now();
}
