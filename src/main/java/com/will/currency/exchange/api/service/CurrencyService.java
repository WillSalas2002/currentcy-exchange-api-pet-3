package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.mapper.CurrencyMapper;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.repository.CurrencyRepository;
import com.will.currency.exchange.api.dto.CurrencyDTO;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private static final String MESSAGE_NOT_FOUND = "There is no Currency with this code";

    private final CurrencyRepository repository = new CurrencyRepository();
    private final CurrencyMapper currencyMapper = CurrencyMapper.INSTANCE;

    public List<CurrencyDTO> findAll() {
        return currencyMapper.toResponseList(repository.findAll());
    }

    public CurrencyDTO findByCurrencyCode(String currencyCode) {
        return repository.findByCurrencyCode(currencyCode)
                .map(currencyMapper::toResponse)
                .orElseThrow(() -> new NoSuchEntityException(MESSAGE_NOT_FOUND));
    }

    public CurrencyDTO save(CurrencyDTO currencyDTO) {
        Currency currency = currencyMapper.toEntity(currencyDTO);
        Currency savedCurrency = repository.save(currency);
        return currencyMapper.toResponse(savedCurrency);
    }
}
