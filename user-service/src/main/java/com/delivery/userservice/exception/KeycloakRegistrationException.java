package com.delivery.userservice.exception;

public class KeycloakRegistrationException extends RuntimeException {
    public KeycloakRegistrationException(String message) {
        super(message);
    }

    public KeycloakRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
