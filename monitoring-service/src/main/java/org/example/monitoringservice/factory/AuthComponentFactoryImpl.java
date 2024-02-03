package org.example.monitoringservice.factory;

import org.example.monitoringservice.configuration.AppConfig;
import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.repository.DbUserRepository;
import org.example.monitoringservice.repository.UserRepository;
import org.example.monitoringservice.service.AuthenticationService;
import org.example.monitoringservice.service.AuthenticationServiceImpl;

public class AuthComponentFactoryImpl extends AppConfig implements AuthComponentFactory {
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
        return new DbUserRepository(getUrl(), getUserName(), getPassword());
    }
}
