package org.example.monitoringservice.in.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.dto.request.UserDto;

import org.example.monitoringservice.dto.response.ResponseDto;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.example.monitoringservice.exception.custom.NotAuthenticatedException;
import org.example.monitoringservice.in.controller.swagger.SwaggerAuthController;
import org.example.monitoringservice.mapper.mapstruct.UserMapper;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.util.ResponseUtil;
import org.example.monitoringservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related operations.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController implements SwaggerAuthController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    /**
     * Endpoint for registering a new user.
     * @param userDto the user information to be registered
     * @return a ResponseEntity with a success message
     */
    @PostMapping("/register")
    @Override
    public ResponseEntity<ResponseDto<?>> registerNewUser(@RequestBody @Valid UserDto userDto) {
        authenticationService.registerUser(userMapper.userDtoToUser(userDto));
        return ResponseEntity.ok(ResponseUtil.okResponse("Регистрация прошла успешно"));
    }

    /**
     * Endpoint for user login.
     * @param loginRequestDto the login request information
     * @return a ResponseEntity with a success message
     */
    @PostMapping( "/login")
    @Override
    public ResponseEntity<ResponseDto<UserResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto)  {
        User user = authenticationService.login(loginRequestDto);
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(userMapper.userToUserResponse(user)));
    }

    /**
     * Endpoint for user logout.
     * @return a ResponseEntity with a success message
     */
    @GetMapping("/logout")
    @Override
    public ResponseEntity<ResponseDto<?>> logout() {
        authenticationService.logout();
        return ResponseEntity.ok(ResponseUtil.okResponse("Вы вышли из системы"));
    }

    /**
     * Endpoint for retrieving the current user's authority information.
     * @return a ResponseEntity with the current user's data
     */
    @GetMapping( "/info")
    @Override
    public ResponseEntity<ResponseDto<?>> getCurrentUserInfo() {
        if (UserContext.isNotAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(userMapper
                        .userToUserResponse(UserContext.getCurrentUser())));
    }
}
