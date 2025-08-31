package com.will.currency.exchange.api.service.exchange;

import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ExchangeDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeStrategyService {
    private final List<ExchangeStrategy> exchangeStrategies;

    public ExchangeStrategyService() {
        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
        this.exchangeStrategies = List.of(
                new DirectExchangeService(exchangeRateRepository),
                new ReversedExchangeStrategy(exchangeRateRepository),
                new USDExchangeService(exchangeRateRepository)
        );
    }

    public Optional<ExchangeDTO> exchange(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal amount) {
        for (ExchangeStrategy exchangeStrategy : exchangeStrategies) {
            Optional<ExchangeDTO> exchange = exchangeStrategy.exchange(baseCurrency, targetCurrency, amount);
            if (exchange.isPresent()) {
                return exchange;
            }
        }
        return Optional.empty();
    }
}
