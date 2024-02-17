package org.example.monitoringservice.exception.custom;

import org.example.monitoringservice.exception.CustomException;
/**
 * NotAuthenticatedException class represents an exception that is thrown
 * when the user is not authenticated.
 */
public class NotAuthenticatedException extends CustomException {
    /**
     * Constructs a new NotAuthenticatedException with the specified detail message.
     *
     */
    public NotAuthenticatedException() {
        super("Вы не авторизованы. Выполните вход в ваш аккаунт");
    }
}
