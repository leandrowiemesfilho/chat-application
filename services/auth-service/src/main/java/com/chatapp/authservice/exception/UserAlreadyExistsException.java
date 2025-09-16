package com.chatapp.authservice.exception;

import com.chatapp.authservice.exception.enums.ErrorType;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException() {
        super(ErrorType.USER_ALREADY_EXISTS);
    }
}
