package org.example.monitoringservice.service;

import org.example.monitoringservice.exception.UserAlreadyExistException;
import org.example.monitoringservice.exception.UserNotFoundException;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.InMemoryUserRepository;
import org.example.monitoringservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private InMemoryUserRepository userRepository;
    private AuthenticationService authenticationService;
    User currentUser;
    User newUser;

    @BeforeEach
    public void setUp() {

        UUID personalAccount = UUID.randomUUID();
        userRepository = new InMemoryUserRepository();
        authenticationService = new AuthenticationService(userRepository);
        currentUser = new User(personalAccount, "user@mail.ru", RoleType.USER, Instant.now());
        newUser = new User(personalAccount, "new@mail.ru", RoleType.USER, Instant.now());
    }

    @Test
    void registerUser_whenRegisterNotExistingUser_thenSuccess() {

        authenticationService.registerUser(newUser);

        assertEquals(newUser, userRepository.findByEmail("new@mail.ru").orElse(null));
    }

    @Test
    void registerUser_whenRegisterUserForExistingUser_expectExceptionThrown() {

        User existingUser = currentUser;
        userRepository.saveUser(existingUser);

        User newUser = currentUser;

        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> authenticationService.registerUser(newUser));
        assertEquals("Пользователь с email user@mail.ru уже зарегистрирован", exception.getMessage());
    }

    @Test
    void loginUser_whenLoginExistingUser_thenSuccess() {

        User testUser = currentUser;
        userRepository.saveUser(testUser);

        authenticationService.login(currentUser.getEmail());

        assertEquals(testUser, UserContext.getCurrentUser());
    }

    @Test
    void loginUser_whenLoginNotExistingUser_expectExceptionThrown() {

        assertThrows(UserNotFoundException.class,
                () -> authenticationService.login("nonexistent@test.com"));
    }

    @Test
    void logoutUser_whenLogout_thenReturnsEmptyUserContext() {
        User testUser = currentUser;
        userRepository.saveUser(testUser);

        authenticationService.login(currentUser.getEmail());

        assertEquals(testUser, UserContext.getCurrentUser());

        authenticationService.logout();

        assertNull(UserContext.getCurrentUser());
    }
}
