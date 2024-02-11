package org.example.monitoringservice.in.controller;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.ResponseEntity;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.example.monitoringservice.mapper.UserMapper;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.UserContext;

import java.text.MessageFormat;

/**
 * Controller for handling authentication-related operations.
 */
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Endpoint for registering a new user.
     * @param userDto the user information to be registered
     * @return a ResponseEntity with a success message
     */
    public ResponseEntity<Object> registerNewUser(UserDto userDto) {
        authenticationService.registerUser(UserMapper.USER_MAPPER.userDtoToUser(userDto));
        return ResponseUtil.okResponse("Регистрация прошла успешно");
    }

    /**
     * Endpoint for user login.
     * @param loginRequestDto the login request information
     * @return a ResponseEntity with a success message
     */
    public ResponseEntity<UserResponseDto> login(LoginRequestDto loginRequestDto)  {
        User user = authenticationService.login(loginRequestDto);
        return ResponseUtil.okResponseWithData(UserMapper.USER_MAPPER.userToUserResponse(user));
    }

    /**
     * Endpoint for user logout.
     * @return a ResponseEntity with a success message
     */
    public ResponseEntity<Object> logout() {
        authenticationService.logout();
        return ResponseUtil.okResponse("Вы вышли из системы");
    }

    /**
     * Endpoint for retrieving the current user's authority information.
     * @return a ResponseEntity with the current user's data
     */
    public ResponseEntity<Object> currentUserAuthority() {
        return ResponseUtil.okResponse(MessageFormat.format(
                        "Ваши личные данные: {0}", UserContext.getCurrentUser()));
    }
}
