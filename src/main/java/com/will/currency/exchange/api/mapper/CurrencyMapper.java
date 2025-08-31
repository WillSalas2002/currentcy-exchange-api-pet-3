package com.will.currency.exchange.api.mapper;

import com.will.currency.exchange.api.dto.request.CurrencyRequestDto;
import com.will.currency.exchange.api.dto.response.CurrencyResponseDto;
import com.will.currency.exchange.api.model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CurrencyMapper {

    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "fullName")
    Currency toEntity(CurrencyResponseDto currencyResponseDto);

    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "fullName")
    @Mapping(source = "sign", target = "sign")
    Currency fromCurrencyRequestToCurrency(CurrencyRequestDto currencyResponseDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "fullName", target = "name")
    CurrencyResponseDto toResponse(Currency currency);

    List<CurrencyResponseDto> toResponseList(List<Currency> currencies);
}
