package org.example.monitoringservice.factory;

import org.example.monitoringservice.in.controller.AuthController;
import org.example.monitoringservice.repository.UserRepository;
import org.example.monitoringservice.service.AuthenticationService;

public interface AuthComponentFactory {

    AuthController createAuthController();
    AuthenticationService createAuthenticationService();
    UserRepository createUserRepository();
}
