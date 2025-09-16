package com.chatapp.authservice.exception;

import com.chatapp.authservice.exception.enums.ErrorType;
import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public BaseException(final ErrorType errorType) {
        super(errorType.getMessage());

        this.status = errorType.getStatus();
        this.errorCode = errorType.name();
    }

    public BaseException(final String message,  final HttpStatus status, final String errorCode) {
        super(message);

        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
