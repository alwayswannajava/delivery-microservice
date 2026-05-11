package com.delivery.userservice.exception;

public class InvalidOAuth2UserException extends RuntimeException {

    public InvalidOAuth2UserException(String message) {
        super(message);
    }
}
