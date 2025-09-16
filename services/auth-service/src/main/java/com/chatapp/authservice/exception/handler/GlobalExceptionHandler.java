package com.chatapp.authservice.exception.handler;

import com.chatapp.authservice.exception.BaseException;
import com.chatapp.authservice.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(final BaseException e) {
        final HttpStatus status = e.getStatus();
        final ErrorResponse response = new ErrorResponse(status.value(),
                status.getReasonPhrase(),
                e.getErrorCode(),
                e.getMessage());

        return new ResponseEntity<>(response, status);
    }
}
