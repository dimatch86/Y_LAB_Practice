package org.example.monitoringservice.exception;

public class DbException extends RuntimeException {
    public DbException(String message) {
        super(message);
    }
}
