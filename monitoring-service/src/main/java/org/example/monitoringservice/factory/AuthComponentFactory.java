package org.example.monitoringservice.factory;

import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.repository.UserRepository;
import org.example.monitoringservice.service.AuthenticationService;
/**
 * A factory interface for creating authentication-related components.
 */
public interface AuthComponentFactory {

    /**
     * Creates an instance of the AuthController.
     * @return the created AuthController
     */
    AuthController createAuthController();
    /**
     * Creates an instance of the AuthenticationService.
     * @return the created AuthenticationService
     */
    AuthenticationService createAuthenticationService();
    /**
     * Creates an instance of the UserRepository.
     * @return the created UserRepository
     */
    UserRepository createUserRepository();
}
