package com.chatapp.authservice.exception.enums;


import org.springframework.http.HttpStatus;

public enum ErrorType {
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "Account locked"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "Invalid or expired token"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User already exists");

    private final HttpStatus status;
    private final String message;

    ErrorType(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
