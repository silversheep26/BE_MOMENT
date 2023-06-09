package com.back.moment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {ApiException.class})
    public ResponseEntity<ErrorResponse> handleApiException(ApiException apiException) {
        return ErrorResponse.toResponseEntity(apiException.getErrorCode());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return ErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        return ErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        e.printStackTrace();
        return ErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
    }
}
