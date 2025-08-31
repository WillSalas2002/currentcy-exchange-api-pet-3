package com.will.currency.exchange.api.exception;

public class DatabaseOperationException extends RuntimeException {
    private final String message;
    public DatabaseOperationException(String message) {
        super(message);
        this.message = message;
    }
}
