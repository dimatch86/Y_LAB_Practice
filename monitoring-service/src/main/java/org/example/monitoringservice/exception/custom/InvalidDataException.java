package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;
/**
 * InvalidDataException class represents an exception that is thrown when the data provided is invalid.
 */
public class InvalidDataException extends CustomException {
    /**
     * Constructs a new InvalidDataException with the specified detail message.
     * @param message the detail message
     */
    public InvalidDataException(String message) {
        super(message);
    }
}
