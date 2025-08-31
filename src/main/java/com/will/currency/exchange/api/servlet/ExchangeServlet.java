package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.ExchangeDTO;
import com.will.currency.exchange.api.exception.CurrencyConversionException;
import com.will.currency.exchange.api.service.exchange.ExchangeStrategyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static com.will.currency.exchange.api.util.Validation.validateCodes;
import static com.will.currency.exchange.api.util.Validation.validateRate;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private static final String PARAM_AMOUNT = "amount";
    private static final String PARAM_BASE_CURRENCY_CODE = "from";
    private static final String PARAM_TARGET_CURRENCY_CODE = "to";

    private final ExchangeStrategyService exchangeService = new ExchangeStrategyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter(PARAM_BASE_CURRENCY_CODE);
        String targetCurrencyCode = req.getParameter(PARAM_TARGET_CURRENCY_CODE);
        String amountStr = req.getParameter(PARAM_AMOUNT);

        validateCodes(baseCurrencyCode, targetCurrencyCode);
        validateRate(amountStr);
        BigDecimal amount = new BigDecimal(amountStr);

        Optional<ExchangeDTO> exchangeOptional = exchangeService.exchange(baseCurrencyCode.toUpperCase(), targetCurrencyCode.toUpperCase(), amount);
        if (exchangeOptional.isPresent()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeOptional.get());
        } else {
            throw new CurrencyConversionException("Couldn't convert");
        }
    }
}
