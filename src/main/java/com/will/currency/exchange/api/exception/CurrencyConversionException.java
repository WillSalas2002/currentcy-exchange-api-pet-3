package com.will.currency.exchange.api.exception;

import lombok.Getter;

@Getter
public class CurrencyConversionException extends RuntimeException {
    private final String message;

    public CurrencyConversionException(String message) {
        super(message);
        this.message = message;
    }
}
