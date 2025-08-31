package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.DatabaseOperationException;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.dto.CurrencyDTO;
import com.will.currency.exchange.api.dto.ErrorResponseDto;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final String SYMBOL_FRONT_SLASH = "/";
    private static final String MESSAGE_INVALID_CURRENCY_CODE = "No such currency: %s";
    private static final String MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error. Try again later";
    private static final String EMPTY_STRING = "";

    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyCode = req.getPathInfo().replace(SYMBOL_FRONT_SLASH, EMPTY_STRING);
        if (Validation.isInvalidCode(currencyCode)) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_CURRENCY_CODE, currencyCode));
            return;
        }
        try {
            CurrencyDTO currencyDTO = currencyService.findByCurrencyCode(currencyCode.toUpperCase());
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencyDTO);
        } catch (NoSuchEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, err.getMessage());
        } catch (DatabaseOperationException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String errorMessage) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto(errorMessage));
    }
}
