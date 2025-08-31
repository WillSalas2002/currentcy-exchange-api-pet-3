package com.will.currency.exchange.api.service.exchange;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ExchangeDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ReversedExchangeStrategy extends ExchangeStrategy {

    public ReversedExchangeStrategy(ExchangeRateRepository exchangeRateRepository) {
        super(exchangeRateRepository);
    }

    @Override
    public Optional<ExchangeDTO> exchange(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal amount) {
        Optional<ExchangeRate> exchangeRateOptional = exchangeRateRepository.findByCurrencyCodes(targetCurrency.getCode(), baseCurrency.getCode());
        if (exchangeRateOptional.isPresent()) {
            ExchangeRate reversedExchangeRate = prepareExchangeRate(exchangeRateOptional.get());
            return Optional.of(calculateExchangeAmount(reversedExchangeRate, amount));
        }
        return Optional.empty();
    }

    private static ExchangeRate prepareExchangeRate(ExchangeRate exchangeRate) {
        BigDecimal reversedRate = BigDecimal.ONE.divide(exchangeRate.getRate(),4, RoundingMode.HALF_EVEN);
        return ExchangeRate.builder()
                .baseCurrency(exchangeRate.getTargetCurrency())
                .targetCurrency(exchangeRate.getBaseCurrency())
                .rate(reversedRate)
                .build();
    }
}
