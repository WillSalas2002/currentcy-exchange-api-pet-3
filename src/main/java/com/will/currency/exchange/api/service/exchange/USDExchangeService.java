package com.will.currency.exchange.api.service.exchange;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ExchangeDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class USDExchangeService extends ExchangeStrategy {

    public USDExchangeService(ExchangeRateRepository exchangeRateRepository) {
        super(exchangeRateRepository);
    }

    @Override
    public Optional<ExchangeDTO> exchange(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal amount) {
        final String USD_CODE = "USD";
        Optional<ExchangeRate> baseToUsdRate = exchangeRateRepository.findByCurrencyCodes(USD_CODE, baseCurrency.getCode());
        Optional<ExchangeRate> targetToUsdRate = exchangeRateRepository.findByCurrencyCodes(USD_CODE, targetCurrency.getCode());
        if (baseToUsdRate.isPresent() && targetToUsdRate.isPresent()) {
            ExchangeRate baseExchangeRate = baseToUsdRate.get();
            ExchangeRate targetExchangeRate = targetToUsdRate.get();
            ExchangeRate exchangeRate = prepareExchangeRate(targetExchangeRate, baseExchangeRate);
            return Optional.of(calculateExchangeAmount(exchangeRate, amount));
        }
        return Optional.empty();
    }

    private static ExchangeRate prepareExchangeRate(ExchangeRate targetExchangeRate, ExchangeRate baseExchangeRate) {
        BigDecimal rateWithUSDBase = targetExchangeRate.getRate().divide(baseExchangeRate.getRate(), 2, RoundingMode.HALF_EVEN);
        return ExchangeRate.builder()
                .baseCurrency(baseExchangeRate.getBaseCurrency())
                .targetCurrency(targetExchangeRate.getTargetCurrency())
                .rate(rateWithUSDBase)
                .build();
    }
}
