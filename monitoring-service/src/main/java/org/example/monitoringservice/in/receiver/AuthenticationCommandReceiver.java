package org.example.monitoringservice.in.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.Response;
import org.example.monitoringservice.exception.BadCredentialsException;
import org.example.monitoringservice.exception.DbException;
import org.example.monitoringservice.exception.UserAlreadyExistException;
import org.example.monitoringservice.exception.UserNotFoundException;
import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.util.UserContext;
import org.example.monitoringservice.util.ValidationUtils;

import java.text.MessageFormat;
import java.util.Scanner;
/**
 * Interaction with the console for authentication actions
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationCommandReceiver {
    private final AuthController authController;

    public void register() {
        if (UserContext.isEntered()) {
            return;
        }
        System.out.println("Введите ваш email");
        String email = new Scanner(System.in).nextLine().toLowerCase().trim();
        System.out.println("Введите пароль");
        String password = new Scanner(System.in).nextLine().toLowerCase().trim();
        System.out.println("Кем вы являетесь (USER или ADMIN)");
        String role = new Scanner(System.in).nextLine().toUpperCase().trim();

        if (isInvalidUserRoleType(role) || isInvalidEmail(email)) {
            return;
        }
        try {
            Response response = authController.registerNewUser(new UserDto(email, password, role));
            log.info(response.getMessage());
            System.out.println("Вы зарегистрировались, войдите в аккаунт с командой \"LOGIN\"");
        } catch (UserAlreadyExistException | DbException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void login() {
        if (UserContext.isEntered()) {
            return;
        }
        System.out.println("Введите ваш email для входа в аккаунт");
        String email = new Scanner(System.in).nextLine().toLowerCase().trim();
        System.out.println("Введите пароль");
        String password = new Scanner(System.in).nextLine().toLowerCase().trim();
        try {
            Response response = authController.login(email, password);
            log.info(response.getMessage());
            System.out.println("Вы вошли в аккаунт. Для вывода всех доступных команд введите \"HELP\"");
        } catch (UserNotFoundException | BadCredentialsException | DbException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void authority() {
        if (UserContext.isNotAuthenticated()) {
            return;
        }
        try {
            Response response = authController.currentUserAuthority();
            log.info(response.getMessage());
            System.out.println(MessageFormat.format("Пользователь {0} с категорией прав {1} - {2}",
                    UserContext.getCurrentUser().getEmail(),
                    UserContext.getCurrentUser().getRole(),
                    response.getDataString()));
        } catch (DbException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

    public void logout() {
        Response response = authController.logout();
        log.info(response.getMessage());
        System.out.println("Вы вышли из аккаунта");
    }
    private boolean isInvalidUserRoleType(String userRole) {
        if (!userRole.matches(ValidationUtils.USER_ROLE_PATTERN)) {
            System.out.println(MessageFormat.format("Некорректный ввод роли. Введите {0} или {1}",
                    RoleType.USER, RoleType.ADMIN));
            return true;
        }
        return false;
    }
    private boolean isInvalidEmail(String email) {
        if (!email.matches(ValidationUtils.EMAIL_PATTERN)) {
            System.out.println("Некорректный email. Пример корректного email - dimatch86@mail.ru");
            return true;
        }
        return false;
    }
}
