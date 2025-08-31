package com.will.currency.exchange.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ExchangeRateDTO {
    private int id;
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;

    public ExchangeRateDTO(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
