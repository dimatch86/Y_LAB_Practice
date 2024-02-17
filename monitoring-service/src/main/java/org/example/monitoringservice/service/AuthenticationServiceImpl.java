package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.aop.Auditable;
import org.example.monitoringservice.aop.Loggable;
import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.exception.custom.BadCredentialsException;
import org.example.monitoringservice.exception.custom.UserAlreadyExistException;
import org.example.monitoringservice.exception.custom.UserNotFoundException;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.UserRepository;
import org.example.monitoringservice.util.UserContext;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;
/**
 * Implementation of the Authentication Service interface.
 */
@Service
@RequiredArgsConstructor
@Loggable
@Auditable
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    /**
     * Registers a new user.
     * @param user the user to be registered
     * @throws UserAlreadyExistException if the user is already registered
     */
    @Override
    public void registerUser(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistException(MessageFormat
                    .format("Пользователь с email {0} уже зарегистрирован", user.getEmail()));
        }
        userRepository.saveUser(user);
    }

    /**
     * Logs a user in.
     * @param loginRequestDto the user's login request
     * @throws UserNotFoundException if the user is not found
     * @throws BadCredentialsException if the provided password is incorrect
     */
    @Override
    public User login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail().toLowerCase()).orElseThrow(() ->
                new UserNotFoundException(MessageFormat
                .format("Пользователь с email {0} не найден", loginRequestDto.getEmail())));

        if (!BCrypt.checkpw(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }
        UserContext.setCurrentUser(user);
        return user;
    }
    @Override
    public void logout() {
        UserContext.setCurrentUser(null);
    }
}
