package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.mapper.UserMapper;
import org.example.monitoringservice.dto.response.Response;
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
        return new Response(MessageFormat.format("Зарегистрирован новый пользователь {0}",
                userDto.getEmail()));
    }

    public Response login(String email, String password)  {
        authenticationService.login(email, password);
        return new Response(MessageFormat.format("Зарегистрирован вход пользователя {0}",
                UserContext.getCurrentUser().getEmail()));
    }

    public Response logout() {
        authenticationService.logout();
        return new Response("Пользователь вышел из системы");
    }

    public Response currentUserAuthority() {
        String authorityInfo = authenticationService.getAuthorityInfo(UserContext.getCurrentUser().getRole().toString());
        return new Response(MessageFormat.format("Пользователь {0} запросил свои полномочия",
                UserContext.getCurrentUser().getEmail()), authorityInfo);
    }
}
