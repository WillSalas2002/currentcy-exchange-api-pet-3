package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.dto.request.CurrencyRequestDto;
import com.will.currency.exchange.api.dto.response.CurrencyResponseDto;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.mapper.CurrencyMapper;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.repository.CurrencyRepository;

import java.util.List;

public class CurrencyService {
    private static final String MESSAGE_NOT_FOUND = "There is no Currency with this code";

    private final CurrencyRepository repository = new CurrencyRepository();
    private final CurrencyMapper currencyMapper = CurrencyMapper.INSTANCE;

    public List<CurrencyResponseDto> findAll() {
        return currencyMapper.toResponseList(repository.findAll());
    }

    public CurrencyResponseDto findByCurrencyCode(String currencyCode) {
        return repository.findByCurrencyCode(currencyCode)
                .map(currencyMapper::toResponse)
                .orElseThrow(() -> new NoSuchEntityException(MESSAGE_NOT_FOUND));
    }

    public CurrencyResponseDto save(CurrencyRequestDto currencyRequestDto) {
        Currency currency = currencyMapper.fromCurrencyRequestToCurrency(currencyRequestDto);
        Currency savedCurrency = repository.save(currency);
        return currencyMapper.toResponse(savedCurrency);
    }
}
