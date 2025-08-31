package com.will.currency.exchange.api.service.exchange;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ExchangeDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class DirectExchangeService extends ExchangeStrategy {

    public DirectExchangeService(ExchangeRateRepository exchangeRateRepository) {
        super(exchangeRateRepository);
    }

    @Override
    public Optional<ExchangeDTO> exchange(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal amount) {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCurrencyCodes(baseCurrency.getCode(), targetCurrency.getCode());
        return exchangeRate.map(rate -> calculateExchangeAmount(rate, amount));
    }
}
