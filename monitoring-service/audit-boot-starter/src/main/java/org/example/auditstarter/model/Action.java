package org.example.auditstarter.model;



import java.time.Instant;

/**
 * This class represents an Action with details such as the method of action,
 * the user who performed the action,
 * and the time the action was created.
 */

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

    public Action(String actionMethod, String actionedBy, Instant createAt) {
        this.actionMethod = actionMethod;
        this.actionedBy = actionedBy;
        this.createAt = createAt;
    }

    public String getActionMethod() {
        return actionMethod;
    }

    public String getActionedBy() {
        return actionedBy;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    }

    public void setActionedBy(String actionedBy) {
        this.actionedBy = actionedBy;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }
}
