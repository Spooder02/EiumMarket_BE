package com.eiummarket.demo.exception;

import org.springframework.http.HttpStatus;

public class BusinessLogicException extends RuntimeException {
    private final HttpStatus httpStatus;

    public BusinessLogicException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}