package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;
/**
 * Exception thrown when a user is not found in the system.
 */
public class UserNotFoundException extends CustomException {
    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
