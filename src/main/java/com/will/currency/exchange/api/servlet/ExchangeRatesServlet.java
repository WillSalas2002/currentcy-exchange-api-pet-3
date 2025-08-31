package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.DatabaseOperationException;
import com.will.currency.exchange.api.exception.DuplicateEntityException;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ErrorResponseDto;
import com.will.currency.exchange.api.dto.ExchangeRateDTO;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.service.ExchangeRateService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private static final String PARAM_RATE = "rate";
    private static final String PARAM_BASE_CURRENCY_CODE = "baseCurrencyCode";
    private static final String PARAM_TARGET_CURRENCY_CODE = "targetCurrencyCode";
    private static final String MESSAGE_INVALID_PARAM = "Invalid parameter: %s";
    private static final String MESSAGE_SAME_CURRENCY_CODES = "Currency codes cannot be the same: %s";
    private static final String MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error. Try again later";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = new CurrencyService();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRateDTO> exchangeRates = exchangeRateService.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeRates);
        } catch (DatabaseOperationException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter(PARAM_BASE_CURRENCY_CODE);
        String targetCurrencyCode = req.getParameter(PARAM_TARGET_CURRENCY_CODE);
        String rateStr = req.getParameter(PARAM_RATE);
        try {
            if (Validation.isInvalidCode(baseCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAM, baseCurrencyCode));
                return;
            }
            if (Validation.isInvalidCode(targetCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAM, targetCurrencyCode));
                return;
            }
            if (Objects.equals(baseCurrencyCode, targetCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_SAME_CURRENCY_CODES, baseCurrencyCode));
                return;
            }
            if (Validation.isInvalidRate(rateStr)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAM, rateStr));
                return;
            }
            baseCurrencyCode = baseCurrencyCode.toUpperCase();
            targetCurrencyCode = targetCurrencyCode.toUpperCase();
            BigDecimal rate = new BigDecimal(rateStr);

            CurrencyDTO baseCurrency = currencyService.findByCurrencyCode(baseCurrencyCode);
            CurrencyDTO targetCurrency = currencyService.findByCurrencyCode(targetCurrencyCode);
            ExchangeRateDTO exchangeRate = new ExchangeRateDTO(baseCurrency, targetCurrency, rate);

            ExchangeRateDTO savedExchangeRate = exchangeRateService.save(exchangeRate);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), savedExchangeRate);

        } catch (NoSuchEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, err.getMessage());
        } catch (DuplicateEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT, err.getMessage());
        } catch (DatabaseOperationException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String err) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto(err));
    }
}
