package com.will.currency.exchange.api.service.exchange;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ExchangeDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public abstract class ExchangeStrategy {
    protected final ExchangeRateRepository exchangeRateRepository;

    protected ExchangeStrategy(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    protected abstract Optional<ExchangeDTO> exchange(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal amount);

    protected ExchangeDTO calculateExchangeAmount(ExchangeRate exchangeRate, BigDecimal amount) {
        BigDecimal convertedAmount = exchangeRate.getRate().multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
        return new ExchangeDTO(exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate(), amount, convertedAmount);
    }
}
