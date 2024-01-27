package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.UserDto;
import org.example.monitoringservice.mapper.UserMapper;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.response.Response;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.util.UserContext;

import java.text.MessageFormat;

/**
 * Controller for processing authentication commands
 */
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper = new UserMapper();

    public Response registerNewUser(UserDto userDto) {
        authenticationService.registerUser(userMapper.userDtoToUser(userDto));
        System.out.println("Вы зарегистрировались, войдите в аккаунт с командой \"LOGIN\"");
        return new Response(MessageFormat.format("Зарегистрирован новый пользователь {0}",
                userDto.getEmail()));
    }

    public Response login(String email) {
        authenticationService.login(email);
        System.out.println("Вы вошли в аккаунт. Для вывода всех доступных команд введите \"HELP\"");
        return new Response(MessageFormat.format("Зарегистрирован вход пользователя {0}",
                UserContext.getCurrentUser().getEmail()));
    }

    public Response logout() {
        authenticationService.logout();
        System.out.println("Вы вышли из аккаунта");
        return new Response("Пользователь вышел из системы");
    }

    public Response currentUserAuthority() {
        System.out.println(MessageFormat.format("Пользователь {0} с категорией прав {1} - {2}",
                UserContext.getCurrentUser().getEmail(),
                UserContext.getCurrentUser().getRole(),
                RoleType.valueOf(UserContext.getCurrentUser().getRole().name()).getDescription()));
        return new Response(MessageFormat.format("Пользователь {0} запросил свои полномочия",
                UserContext.getCurrentUser().getEmail()));
    }
}
