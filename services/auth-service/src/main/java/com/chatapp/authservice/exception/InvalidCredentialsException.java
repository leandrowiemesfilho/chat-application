package com.chatapp.authservice.exception;

import com.chatapp.authservice.exception.enums.ErrorType;

public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException() {
        super(ErrorType.INVALID_CREDENTIALS);
    }
}
