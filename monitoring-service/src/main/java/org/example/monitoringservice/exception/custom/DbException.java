package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;
/**
 * Exception class for representing database-related errors.
 */
public class DbException extends CustomException {
    /**
     * Constructs a new DbException with the specified error message.
     * @param message the error message
     */
    public DbException(String message) {
        super(message);
    }
}
