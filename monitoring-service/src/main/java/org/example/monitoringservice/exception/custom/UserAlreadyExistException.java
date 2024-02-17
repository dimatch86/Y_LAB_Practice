package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;
/**
 * Exception thrown when a user with the same login or email already exists in the system.
 */
public class UserAlreadyExistException extends CustomException {
    /**
     * Constructs a new UserAlreadyExistException with the specified detail message.
     * @param message the detail message
     */
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
