package org.example.monitoringservice.model.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.monitoringservice.util.UserContext;

import java.time.Instant;
/**
 * This class represents an Action with details such as the method of action,
 * the user who performed the action,
 * and the time the action was created.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Action {

    private String actionMethod;

    /**
     * The user who performed the action. Defaults to the current user's email in UserContext.
     */
    @Builder.Default
    private String actionedBy = UserContext.getCurrentUser().getEmail();

    /**
     * The time when the action was created. Defaults to the current time.
     */
    @Builder.Default
    private Instant createAt = Instant.now();
}
