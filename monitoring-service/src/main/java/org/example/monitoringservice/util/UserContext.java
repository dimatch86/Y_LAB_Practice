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

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isNotAuthenticated() {
        if (currentUser == null) {
            System.out.println("Вы не авторизованы. Выполните вход в ваш аккаунт");
            return true;
        }
        return false;
    }

    public boolean isNotAdmin() {
        if (!currentUser.getRole().equals(RoleType.ADMIN)) {
            System.out.println("У вас недостаточно прав для данного действия");
            return true;
        }
        return false;
    }
}
