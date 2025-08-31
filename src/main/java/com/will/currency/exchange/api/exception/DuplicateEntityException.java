package com.will.currency.exchange.api.exception;

import lombok.Getter;

@Getter
public class DuplicateEntityException extends RuntimeException {
    private final String message;

    public DuplicateEntityException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
