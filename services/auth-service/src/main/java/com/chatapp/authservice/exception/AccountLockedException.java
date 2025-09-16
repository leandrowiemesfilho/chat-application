package com.chatapp.authservice.exception;

import com.chatapp.authservice.exception.enums.ErrorType;

public class AccountLockedException extends BaseException {

    public AccountLockedException() {
        super(ErrorType.ACCOUNT_LOCKED);
    }
}
