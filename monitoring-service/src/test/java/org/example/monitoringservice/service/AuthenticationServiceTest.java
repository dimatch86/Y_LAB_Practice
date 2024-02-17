package org.example.monitoringservice.service;

import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.exception.custom.BadCredentialsException;
import org.example.monitoringservice.exception.custom.UserAlreadyExistException;
import org.example.monitoringservice.exception.custom.UserNotFoundException;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.UserRepositoryImpl;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final UserRepositoryImpl userRepository = mock(UserRepositoryImpl.class);
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl(userRepository);
    User currentUser;
    User newUser;
    UUID personalAccount;

    @BeforeEach
    public void setUp() {

        personalAccount = UUID.randomUUID();

        currentUser = new User(personalAccount, "user@mail.ru", "testPassword", RoleType.USER, Instant.now());
        newUser = new User(personalAccount, "new@mail.ru", "passwordTest", RoleType.USER, Instant.now());
    }

    @AfterEach
    public void tearDown() {
        UserContext.setCurrentUser(null);
    }

    @Test
    void registerUser_whenRegisterNotExistingUser_thenSuccess() {

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());

        authenticationService.registerUser(newUser);

        verify(userRepository, times(1))
                .saveUser(newUser);
    }

    @Test
    void registerUser_whenRegisterUserForExistingUser_expectExceptionThrown() {
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));

        assertThatThrownBy(() -> authenticationService.registerUser(newUser))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("Пользователь с email new@mail.ru уже зарегистрирован");
        verify(userRepository, times(0))
                .saveUser(newUser);
    }

    @Test
    void loginUser_whenLoginExistedUser_thenSuccess() {
        String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());

        when(userRepository.findByEmail(newUser.getEmail()))
                .thenReturn(Optional.of(new User(personalAccount, "new@mail.ru", hashedPassword, RoleType.USER, Instant.now())));

        authenticationService.login(new LoginRequestDto(newUser.getEmail(), newUser.getPassword()));

        assertNotNull(UserContext.getCurrentUser());
        assertThat(UserContext.getCurrentUser().getEmail()).isEqualTo("new@mail.ru");
    }

    @Test
    void loginUser_whenLoginNotExistedUser_expectExceptionThrown() {

        when(userRepository.findByEmail(newUser.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.login(new LoginRequestDto(newUser.getEmail(), newUser.getPassword())))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь с email new@mail.ru не найден");
        assertNull(UserContext.getCurrentUser());
    }

    @Test
    void loginUser_whenLoginWithWrongPassword_expectExceptionThrown() {

        String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());

        when(userRepository.findByEmail(newUser.getEmail()))
                .thenReturn(Optional.of(new User(personalAccount, "new@mail.ru", hashedPassword, RoleType.USER, Instant.now())));

        assertThatThrownBy(() -> authenticationService.login(new LoginRequestDto(newUser.getEmail(), "wrong password")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Неверный пароль");
        assertNull(UserContext.getCurrentUser());
    }
}
