package org.example.monitoringservice.in.receiver;

import lombok.extern.slf4j.Slf4j;
import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.dto.UserDto;
import org.example.monitoringservice.exception.UserAlreadyExistException;
import org.example.monitoringservice.exception.UserNotFoundException;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.repository.InMemoryUserRepository;
import org.example.monitoringservice.response.Response;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.util.UserContext;
import org.example.monitoringservice.util.ValidationUtils;

import java.text.MessageFormat;
import java.util.Scanner;
/**
 * Interaction with the console for authentication actions
 */
@Slf4j
public class AuthenticationCommandReceiver {

    private final AuthController authController =
            new AuthController(new AuthenticationService(new InMemoryUserRepository()));

    public void register() {
        System.out.println("Введите ваш email");
        String email = new Scanner(System.in).nextLine().toLowerCase();
        System.out.println("Кем вы являетесь (USER или ADMIN)");
        String role = new Scanner(System.in).nextLine().toUpperCase();

        if (isInvalidUserRoleType(role) || isInvalidEmail(email)) {
            return;
        }
        try {
            Response response = authController.registerNewUser(new UserDto(email, role));
            log.info(response.getMessage());
        } catch (UserAlreadyExistException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void login() {
        if (UserContext.getCurrentUser() != null) {
            System.out.println("Вход в аккаунт уже выполнен");
            return;
        }
        System.out.println("Введите ваш email для входа в аккаунт");
        String email = new Scanner(System.in).nextLine().toLowerCase().trim();
        try {
            Response response = authController.login(email);
            log.info(response.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void authority() {
        Response response = authController.currentUserAuthority();
        log.info(response.getMessage());
    }

    public void logout() {
        Response response = authController.logout();
        log.info(response.getMessage());
    }
    private boolean isInvalidUserRoleType(String userRole) {
        if (!userRole.matches(ValidationUtils.USER_ROLE_PATTERN)) {
            System.out.println(MessageFormat.format("Некорректный ввод роли. Введите {0} или {1}",
                    RoleType.USER.getRole(), RoleType.ADMIN.getRole()));
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
