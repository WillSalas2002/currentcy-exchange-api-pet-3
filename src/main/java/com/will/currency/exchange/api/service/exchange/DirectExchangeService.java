package com.will.currency.exchange.api.service.exchange;

import com.will.currency.exchange.api.dto.ExchangeDTO;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class DirectExchangeService extends ExchangeStrategy {

    public DirectExchangeService(ExchangeRateRepository exchangeRateRepository) {
        super(exchangeRateRepository);
    }

    @Override
    public Optional<ExchangeDTO> exchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        return exchangeRate.map(rate -> calculateExchangeAmount(rate, amount));
    }
}
