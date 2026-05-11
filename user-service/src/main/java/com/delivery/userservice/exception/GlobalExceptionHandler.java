package com.delivery.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateUser(DuplicateUserException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(KeycloakRegistrationException.class)
    public ResponseEntity<ProblemDetail> handleKeycloakRegistration(KeycloakRegistrationException exception) {
        return buildResponse(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(InvalidOAuth2UserException.class)
    public ResponseEntity<ProblemDetail> handleInvalidOAuth2User(InvalidOAuth2UserException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ProblemDetail> handleInvalidLogin(InvalidLoginException exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatus status, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        return ResponseEntity.status(status).body(problemDetail);
    }
}
