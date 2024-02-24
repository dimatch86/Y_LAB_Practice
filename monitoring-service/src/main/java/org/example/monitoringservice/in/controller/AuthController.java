package org.example.monitoringservice.in.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.AuthResponseDto;
import org.example.monitoringservice.dto.response.ResponseDto;
import org.example.monitoringservice.in.controller.swagger.SwaggerAuthController;
import org.example.monitoringservice.mapper.mapstruct.UserMapper;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
     *
     * @param loginRequestDto the login request information
     * @return a ResponseEntity with a success message
     */
    @PostMapping( "/login")
    @Override
    public ResponseEntity<ResponseDto<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto)  {
        AuthResponseDto authResponseDto = authenticationService.login(loginRequestDto);
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(authResponseDto));
    }

    /**
     * Endpoint for retrieving the current user's authority information.
     * @return a ResponseEntity with the current user's data
     */
    @GetMapping( "/info")
    @Override
    public ResponseEntity<ResponseDto<?>> getCurrentUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticationService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ResponseUtil.okResponseWithData(userMapper
                        .userToUserResponse(user)));
    }
}
