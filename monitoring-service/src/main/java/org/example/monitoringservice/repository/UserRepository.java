package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.user.User;

import java.util.Optional;
/**
 * Interface for user repository.
 */
public interface UserRepository {

    /**
     * Saves the user.
     * @param user The user to be saved
     */
    void saveUser(User user);

    /**
     * Finds a user by email.
     * @param userEmail The email of the user to find
     * @return An Optional containing the user if found
     */
    Optional<User> findByEmail(String userEmail);
}
