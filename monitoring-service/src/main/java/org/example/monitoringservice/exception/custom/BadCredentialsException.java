package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;
/**
 * BadCredentialsException class represents an exception that is
 * thrown when the user credentials are invalid.
 */
public class BadCredentialsException extends CustomException {
    /**
     * Constructs a new BadCredentialsException with the specified detail message.
     * @param message the detail message
     */
    public BadCredentialsException(String message) {
        super(message);
    }
}
