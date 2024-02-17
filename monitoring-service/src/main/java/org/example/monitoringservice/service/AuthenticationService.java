package org.example.monitoringservice.service;

import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.model.user.User;

/**
 * Interface for handling user authentication.
 */
public interface AuthenticationService {

    /**
     * Registers a new user.
     * @param user the user to be registered
     */
    void registerUser(User user);

    /**
     * Logs a user in.
     * @param loginRequestDto the user's login request
     */
    User login(LoginRequestDto loginRequestDto);

    /**
     * Logs a user out.
     */
    void logout();
}
