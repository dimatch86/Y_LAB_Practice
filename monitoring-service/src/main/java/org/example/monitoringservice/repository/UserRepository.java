package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.user.User;

import java.util.Optional;

public interface UserRepository {

    void saveUser(User user);
    Optional<User> findByEmail(String userEmail);
}
