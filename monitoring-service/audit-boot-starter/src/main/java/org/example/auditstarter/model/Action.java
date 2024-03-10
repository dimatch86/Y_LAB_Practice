package org.example.auditstarter.model;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String actionedBy;

    /**
     * The time when the action was created. Defaults to the current time.
     */
    private Instant createAt;
}
