package com.chatapp.security.exception;

public class EncryptionException extends RuntimeException {
    public EncryptionException(final String message) {
        super(message);
    }

    public EncryptionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
