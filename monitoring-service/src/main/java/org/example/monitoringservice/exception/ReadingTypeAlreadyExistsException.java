package org.example.monitoringservice.exception;

public class ReadingTypeAlreadyExistsException extends RuntimeException {
    public ReadingTypeAlreadyExistsException(String message) {
        super(message);
    }
}
