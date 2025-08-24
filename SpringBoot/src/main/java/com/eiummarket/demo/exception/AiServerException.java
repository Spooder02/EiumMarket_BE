package com.eiummarket.demo.exception;

public class AiServerException extends RuntimeException {
    private final int statusCode;

    public AiServerException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}