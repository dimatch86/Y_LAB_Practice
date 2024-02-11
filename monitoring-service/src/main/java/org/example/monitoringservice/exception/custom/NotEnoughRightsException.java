package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;
/**
 * Exception thrown when a user does not have enough rights to perform a certain action.
 */
public class NotEnoughRightsException extends CustomException {
    /**
     * Constructs a new NotEnoughRightsException with the specified detail message.
     * @param message the detail message
     */
    public NotEnoughRightsException(String message) {
        super(message);
    }
}
