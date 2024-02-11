package org.example.monitoringservice.exception;
/**
 * Custom exception that extends RuntimeException, used as a base class for custom exceptions.
 */
public class CustomException extends RuntimeException {
    /**
     * Constructs a new CustomException with the specified detail message.
     * @param message the detail message
     */
    public CustomException(String message) {
        super(message);
    }
}
