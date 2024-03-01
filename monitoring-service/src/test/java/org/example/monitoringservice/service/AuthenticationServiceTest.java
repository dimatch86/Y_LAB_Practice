package org.example.monitoringservice.service;

import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.dto.response.AuthResponseDto;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.UserRepositoryImpl;
import org.example.monitoringservice.security.AppUserDetails;
import org.example.monitoringservice.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final UserRepositoryImpl userRepository = mock(UserRepositoryImpl.class);
    private final JwtUtils jwtUtils = mock(JwtUtils.class);
    Authentication authentication = mock(Authentication.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl(userRepository, authenticationManager, jwtUtils);
    User currentUser;
    User newUser;
    UUID personalAccount;

    @BeforeEach
    public void setUp() {

        personalAccount = UUID.randomUUID();

        currentUser = new User(personalAccount, "user@mail.ru", "testPassword", RoleType.ROLE_USER, Instant.now());
        newUser = new User(personalAccount, "new@mail.ru", "passwordTest", RoleType.ROLE_USER, Instant.now());
    }

    @Test
    void registerUser_whenRegisterNotExistingUser_thenSuccess() {

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());

        authenticationService.registerUser(newUser);

        verify(userRepository, times(1))
                .saveUser(newUser);
    }

    @Test
    void testLogin() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
        User user = User.builder().email("test@example.com").password("12121").role(RoleType.ROLE_USER).build();

        AppUserDetails userDetails = new AppUserDetails(user);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("jwtToken");

        AuthResponseDto authResponseDto = authenticationService.login(loginRequestDto);
        assertThat(authResponseDto.getToken()).isEqualTo("jwtToken");
        assertThat(authResponseDto.getEmail()).isEqualTo("test@example.com");
        assertThat(authResponseDto.getRoles()).isEqualTo(Collections.singletonList("ROLE_USER"));
    }
}
