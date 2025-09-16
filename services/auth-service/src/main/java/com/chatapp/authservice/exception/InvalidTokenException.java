package com.chatapp.authservice.exception;

import com.chatapp.authservice.exception.enums.ErrorType;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException() {
        super(ErrorType.INVALID_TOKEN);
    }
}
