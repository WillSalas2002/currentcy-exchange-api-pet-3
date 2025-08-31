package com.will.currency.exchange.api.mapper;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.dto.ExchangeRateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "baseCurrency", target = "baseCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    @Mapping(source = "rate", target = "rate")
    ExchangeRate toEntity(ExchangeRateDTO exchangeRateDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "baseCurrency", target = "baseCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    @Mapping(source = "rate", target = "rate")
    ExchangeRateDTO toResponse(ExchangeRate exchangeRate);

    List<ExchangeRateDTO> toResponseList(List<ExchangeRate> exchangeRates);
}
