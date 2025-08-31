package com.will.currency.exchange.api.util;

import com.will.currency.exchange.api.dto.request.ExchangeRateRequestDto;
import com.will.currency.exchange.api.exception.BadRequestException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static java.lang.String.format;

@UtilityClass
public class Validation {
    private static final String MESSAGE_INVALID_PARAMETER = "Invalid parameter: %s";
    private static final String MESSAGE_INVALID_CURRENCY_CODE = "No such currency: %s";

    private static final Set<String> VALID_CURRENCY_SYMBOLS = new HashSet<>();

    static {
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                VALID_CURRENCY_SYMBOLS.add(currency.getSymbol(locale));
            } catch (Exception ignored) {
                // Some locales don't have a currency
            }
        }
    }

    public static void validateCode(String code) {
        try {
            Currency.getInstance(code.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new BadRequestException(format(MESSAGE_INVALID_CURRENCY_CODE, code));
        }
    }

    public static void validateName(String name) {
        if (name == null || name.length() <= 1 || name.length() > 100) {
            throw new BadRequestException(format(MESSAGE_INVALID_PARAMETER, name));
        }
    }

    public static void validateSign(String sign) {
        if (!VALID_CURRENCY_SYMBOLS.contains(sign)) {
            throw new BadRequestException(format(MESSAGE_INVALID_PARAMETER, sign));
        }
    }

    public static void validateRate(String rate) {
        if (rate == null ||
                !rate.matches("^\\d+(\\.\\d+)?$") ||
                new BigDecimal(rate).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(format(MESSAGE_INVALID_PARAMETER, rate));
        }
    }

    public static void validatePath(String path) {
        if (path == null || path.length() != 6) {
            throw new BadRequestException(format(MESSAGE_INVALID_PARAMETER, path));
        }
    }

    public static void validateExchangeRate(ExchangeRateRequestDto exchangeRateRequest) {
        validateCodes(exchangeRateRequest.baseCurrencyCode(), exchangeRateRequest.targetCurrencyCode());
        validateRate(exchangeRateRequest.rate());
    }

    public static void validateCodes(String baseCurrencyCode, String targetCurrencyCode) {
        validateCode(baseCurrencyCode);
        validateCode(targetCurrencyCode);
        if (baseCurrencyCode.equalsIgnoreCase(targetCurrencyCode)) {
            throw new BadRequestException("Currency codes shouldn't be same");
        }
    }
}
