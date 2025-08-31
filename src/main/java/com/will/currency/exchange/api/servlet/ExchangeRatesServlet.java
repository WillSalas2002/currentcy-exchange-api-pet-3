package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.request.ExchangeRateRequestDto;
import com.will.currency.exchange.api.dto.response.ExchangeRateResponseDto;
import com.will.currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.will.currency.exchange.api.util.Validation.validateExchangeRate;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private static final String PARAM_RATE = "rate";
    private static final String PARAM_BASE_CURRENCY_CODE = "baseCurrencyCode";
    private static final String PARAM_TARGET_CURRENCY_CODE = "targetCurrencyCode";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> exchangeRates = exchangeRateService.findAll();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto exchangeRateRequest = convertToDto(req);

        ExchangeRateResponseDto savedExchangeRate = exchangeRateService.save(exchangeRateRequest);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), savedExchangeRate);
    }

    private static ExchangeRateRequestDto convertToDto(HttpServletRequest req) {
        String baseCurrencyCode = req.getParameter(PARAM_BASE_CURRENCY_CODE);
        String targetCurrencyCode = req.getParameter(PARAM_TARGET_CURRENCY_CODE);
        String rateStr = req.getParameter(PARAM_RATE);

        ExchangeRateRequestDto exchangeRateRequest = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rateStr);
        validateExchangeRate(exchangeRateRequest);
        return exchangeRateRequest;
    }
}
