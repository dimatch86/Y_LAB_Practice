package org.example.monitoringservice.util;

import lombok.experimental.UtilityClass;
import org.example.monitoringservice.model.user.User;

/**
 * Utility class for storage authenticated user
 */
@UtilityClass
public class UserContext {
    private User currentUser;

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
