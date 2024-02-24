package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;

public class BlackListTokenException extends CustomException {

    public BlackListTokenException(String message) {
        super(message);
    }
}
