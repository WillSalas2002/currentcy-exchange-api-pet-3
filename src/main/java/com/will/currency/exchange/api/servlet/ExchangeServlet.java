package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.BadRequestException;
import com.will.currency.exchange.api.exception.DatabaseOperationException;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ErrorResponseDto;
import com.will.currency.exchange.api.dto.ExchangeDTO;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.service.exchange.ExchangeStrategyService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private static final String PARAM_AMOUNT = "amount";
    private static final String PARAM_BASE_CURRENCY_CODE = "from";
    private static final String PARAM_TARGET_CURRENCY_CODE = "to";
    private static final String MESSAGE_INVALID_AMOUNT = "Invalid amount: \"%s\"";
    private static final String MESSAGE_INVALID_CURRENCY_CODE = "No such currency: %s";
    private static final String MESSAGE_SAME_CURRENCY_CODES = "Currency codes cannot be the same: %s";
    private static final String MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error. Try again later";

    private final ExchangeStrategyService exchangeService = new ExchangeStrategyService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter(PARAM_BASE_CURRENCY_CODE);
        String targetCurrencyCode = req.getParameter(PARAM_TARGET_CURRENCY_CODE);
        String amountStr = req.getParameter(PARAM_AMOUNT);

        try {
            if (Validation.isInvalidCode(baseCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_CURRENCY_CODE, baseCurrencyCode));
                return;
            }
            if (Validation.isInvalidCode(targetCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_CURRENCY_CODE, targetCurrencyCode));
                return;
            }
            if (Objects.equals(baseCurrencyCode, targetCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_SAME_CURRENCY_CODES, baseCurrencyCode));
                return;
            }
            if (Validation.isInvalidRate(amountStr)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_AMOUNT, amountStr));
                return;
            }
            CurrencyDTO baseCurrency = currencyService.findByCurrencyCode(baseCurrencyCode);
            CurrencyDTO targetCurrency = currencyService.findByCurrencyCode(targetCurrencyCode);
            BigDecimal amount = new BigDecimal(amountStr);
            Optional<ExchangeDTO> exchangeOptional = exchangeService.exchange(baseCurrency, targetCurrency, amount);
            if (exchangeOptional.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), exchangeOptional.get());
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Couldn't convert");
            }
        } catch (BadRequestException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, err.getMessage());
        } catch (NoSuchEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, err.getMessage());
        } catch (DatabaseOperationException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String messageInternalServerError) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto(messageInternalServerError));
    }
}
