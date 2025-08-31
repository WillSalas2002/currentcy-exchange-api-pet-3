package com.will.currency.exchange.api.dto.request;

public record ExchangeRateRequestDto(
        String baseCurrencyCode,
        String targetCurrencyCode,
        String rate) {
}
