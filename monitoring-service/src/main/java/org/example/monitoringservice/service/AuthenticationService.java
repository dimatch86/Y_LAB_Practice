package org.example.monitoringservice.service;

import org.example.monitoringservice.model.user.User;


public interface AuthenticationService {

    void registerUser(User user);
    void login(String userEmail, String password);
    void logout();

    String getAuthorityInfo(String roleType);
}
