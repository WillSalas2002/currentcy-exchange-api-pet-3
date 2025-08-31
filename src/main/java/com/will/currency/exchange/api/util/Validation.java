package com.will.currency.exchange.api.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@UtilityClass
public class Validation {

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

    public static boolean isInvalidCode(String code) {
        if (code == null) return true;
        try {
            Currency.getInstance(code.toUpperCase());
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    public static boolean isValidFullName(String fullName) {
        return fullName != null && fullName.length() > 1 && fullName.length() <= 100;
    }

    public static boolean isValidSign(String sign) {
        return VALID_CURRENCY_SYMBOLS.contains(sign);
    }

    public static boolean isInvalidRate(String rate) {
        return rate == null ||
                !rate.matches("^\\d+(\\.\\d+)?$") ||
                new BigDecimal(rate).compareTo(BigDecimal.ZERO) <= 0;
    }

    public static boolean isValidExchangeRatePath(String path) {
        return path == null || path.length() != 6;
    }
}
