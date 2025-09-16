package com.chatapp.authservice.exception.dto;

public record ErrorResponse(int status,
                            String error,
                            String errorCode,
                            String message) {
}
