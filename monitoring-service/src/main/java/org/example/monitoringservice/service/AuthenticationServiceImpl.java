package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.BadCredentialsException;
import org.example.monitoringservice.exception.UserAlreadyExistException;
import org.example.monitoringservice.exception.UserNotFoundException;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.UserRepository;
import org.example.monitoringservice.util.UserContext;
import org.mindrot.jbcrypt.BCrypt;

import java.text.MessageFormat;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    @Override
    public void registerUser(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistException(MessageFormat
                    .format("Пользователь с email {0} уже зарегистрирован", user.getEmail()));
        }
        userRepository.saveUser(user);
    }

    @Override
    public void login(String userEmail, String password) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new UserNotFoundException(MessageFormat
                .format("Пользователь с email {0} не найден", userEmail)));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }
        UserContext.setCurrentUser(user);
    }
    @Override
    public void logout() {
        UserContext.setCurrentUser(null);
    }

    @Override
    public String getAuthorityInfo(String roleType) {
        return userRepository.getAuthorityInfo(roleType);
    }
}
