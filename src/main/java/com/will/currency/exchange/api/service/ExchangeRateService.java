package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.response.ExchangeRateResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRateService {
    private final ExchangeRateRepository repository = new ExchangeRateRepository();

    public List<ExchangeRateResponse> findAll() throws SQLException {
        return repository.findAll()
                .stream()
                .map(this::convertToExchangeRateResponse)
                .collect(Collectors.toList());
    }

    public ExchangeRateResponse save(ExchangeRateResponse exchangeRateResponse) throws SQLException {
        ExchangeRate exchangeRate = convertToExchangeRate(exchangeRateResponse);
        ExchangeRate savedExchangeRate = repository.save(exchangeRate);
        return convertToExchangeRateResponse(savedExchangeRate);
    }

    public ExchangeRateResponse findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> exchangeRateOptional = repository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRateOptional.isPresent()) {
            return convertToExchangeRateResponse(exchangeRateOptional.get());
        }
        throw new NoSuchEntityException("Exchange rate with this codes not found");
    }

    public ExchangeRateResponse update(ExchangeRateResponse exchangeRateResponse) throws SQLException {
        ExchangeRate exchangeRate = convertToExchangeRate(exchangeRateResponse);
        ExchangeRate updatedExchangeRate = repository.update(exchangeRate);
        return convertToExchangeRateResponse(updatedExchangeRate);
    }

    private ExchangeRate convertToExchangeRate(ExchangeRateResponse exchangeRateResponse) {
        return new ExchangeRate(
                exchangeRateResponse.getId(),
                convertToCurrency(exchangeRateResponse.getBaseCurrency()),
                convertToCurrency(exchangeRateResponse.getTargetCurrency()),
                exchangeRateResponse.getRate()
        );
    }

    public ExchangeRateResponse convertToExchangeRateResponse(ExchangeRate exchangeRate) {
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                convertToCurrencyResponse(exchangeRate.getBaseCurrency()),
                convertToCurrencyResponse(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate()
        );
    }

    private CurrencyResponse convertToCurrencyResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }

    private Currency convertToCurrency(CurrencyResponse currencyResponse) {
        return new Currency(
                currencyResponse.getId(),
                currencyResponse.getCode(),
                currencyResponse.getFullName(),
                currencyResponse.getSign()
        );
    }
}
