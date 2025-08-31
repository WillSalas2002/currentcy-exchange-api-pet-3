package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.request.ExchangeRateRequestDto;
import com.will.currency.exchange.api.dto.response.ExchangeRateResponseDto;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.mapper.ExchangeRateMapper;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.CurrencyRepository;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.String.format;

public class ExchangeRateService {
    private static final String MESSAGE_NOT_FOUND = "Exchange rate with this codes not found";
    private final ExchangeRateRepository repository = new ExchangeRateRepository();
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;

    public List<ExchangeRateResponseDto> findAll() {
        return exchangeRateMapper.toResponseList(
                repository.findAll()
        );
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto exchangeRateRequest) {
        String baseCurrencyCode = exchangeRateRequest.baseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequest.targetCurrencyCode();
        BigDecimal rate = new BigDecimal(exchangeRateRequest.rate());

        Currency baseCurrency = currencyRepository.findByCurrencyCode(baseCurrencyCode)
                .orElseThrow(() -> new NoSuchEntityException(format("Currency doesn't exists: %s", baseCurrencyCode)));
        Currency targetCurrency = currencyRepository.findByCurrencyCode(targetCurrencyCode)
                .orElseThrow(() -> new NoSuchEntityException(format("Currency doesn't exists: %s", targetCurrencyCode)));
        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

        ExchangeRate savedExchangeRate = repository.save(exchangeRate);
        return exchangeRateMapper.toResponse(savedExchangeRate);
    }

    public ExchangeRateResponseDto findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return repository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode)
                .map(exchangeRateMapper::toResponse)
                .orElseThrow(() -> new NoSuchEntityException(MESSAGE_NOT_FOUND));
    }

    public ExchangeRateResponseDto update(ExchangeRateRequestDto exchangeRateRequest) {
        String baseCurrencyCode = exchangeRateRequest.baseCurrencyCode().toUpperCase();
        String targetCurrencyCode = exchangeRateRequest.targetCurrencyCode().toUpperCase();
        ExchangeRate exchangeRate = repository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NoSuchEntityException(format("No such currency codes: %s and %s", baseCurrencyCode, targetCurrencyCode)));
        exchangeRate.setRate(new BigDecimal(exchangeRateRequest.rate()));

        ExchangeRate updatedExchangeRate = repository.update(exchangeRate);
        return exchangeRateMapper.toResponse(updatedExchangeRate);
    }
}
