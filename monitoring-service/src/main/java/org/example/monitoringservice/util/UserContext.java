package org.example.monitoringservice.util;

import lombok.experimental.UtilityClass;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;

/**
 * Utility class for storage authenticated user
 */
@UtilityClass
public class UserContext {
    private User currentUser;

    /**
     * Sets the current user.
     * @param user the user to be set as the current user
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Retrieves the current user.
     * @return the current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if the user is not authenticated.
     * @return true if the user is not authenticated, false otherwise
     */
    public boolean isNotAuthenticated() {
        return currentUser == null;
    }

    /**
     * Checks if the user is not an admin.
     * @return true if the user is not an admin, false otherwise
     */
    public boolean isNotAdmin() {
        return !currentUser.getRole().equals(RoleType.ADMIN);
    }
}
