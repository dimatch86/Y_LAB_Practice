package org.example.monitoringservice.factory;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.repository.DbUserRepository;
import org.example.monitoringservice.repository.UserRepository;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.service.AuthenticationServiceImpl;

/**
 * A factory implementation for creating authentication-related components.
 */
@RequiredArgsConstructor
public class AuthComponentFactoryImpl implements AuthComponentFactory {

    private final String url;
    private final String userName;
    private final String password;
    @Override
    public AuthController createAuthController() {
        return new AuthController(createAuthenticationService());
    }

    @Override
    public AuthenticationService createAuthenticationService() {
        return new AuthenticationServiceImpl(createUserRepository());
    }

    @Override
    public UserRepository createUserRepository() {
        return new DbUserRepository(url, userName, password);
    }
}
