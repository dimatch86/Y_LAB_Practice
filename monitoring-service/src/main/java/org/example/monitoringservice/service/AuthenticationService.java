package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.UserAlreadyExistException;
import org.example.monitoringservice.exception.UserNotFoundException;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.InMemoryUserRepository;
import org.example.monitoringservice.util.UserContext;

import java.text.MessageFormat;

@RequiredArgsConstructor
public class AuthenticationService {
    private final InMemoryUserRepository inMemoryUserRepository;

    public void registerUser(User user) {
        if (inMemoryUserRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistException(MessageFormat
                    .format("Пользователь с email {0} уже зарегистрирован", user.getEmail()));
        }
        inMemoryUserRepository.saveUser(user);
    }

    public void login(String userEmail) {
        User user = inMemoryUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(MessageFormat
                        .format("Пользователь с email {0} не найден", userEmail)));
        UserContext.setCurrentUser(user);
    }
    public void logout() {
        UserContext.setCurrentUser(null);
    }
}
