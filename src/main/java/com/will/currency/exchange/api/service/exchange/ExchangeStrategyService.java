package com.will.currency.exchange.api.service.exchange;

import com.will.currency.exchange.api.dto.ExchangeDTO;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;

import java.math.BigDecimal;
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

    public Optional<ExchangeDTO> exchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        for (ExchangeStrategy exchangeStrategy : exchangeStrategies) {
            Optional<ExchangeDTO> exchange = exchangeStrategy.exchange(baseCurrencyCode, targetCurrencyCode, amount);
            if (exchange.isPresent()) {
                return exchange;
            }
        }
        return Optional.empty();
    }
}
