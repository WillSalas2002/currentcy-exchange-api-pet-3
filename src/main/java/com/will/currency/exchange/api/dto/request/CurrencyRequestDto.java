package com.will.currency.exchange.api.dto.request;

public record CurrencyRequestDto(
        String code,
        String name,
        String sign) {
}
