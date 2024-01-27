package org.example.monitoringservice.exception;

public class TooRecentReadingException extends RuntimeException {
    public TooRecentReadingException(String message) {
        super(message);
    }
}
