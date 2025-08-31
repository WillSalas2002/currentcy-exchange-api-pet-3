package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.response.CurrencyResponseDto;
import com.will.currency.exchange.api.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.will.currency.exchange.api.util.Validation.validateCode;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final String SYMBOL_FRONT_SLASH = "/";
    private static final String EMPTY_STRING = "";

    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyCode = req.getPathInfo().replace(SYMBOL_FRONT_SLASH, EMPTY_STRING);
        validateCode(currencyCode);
        CurrencyResponseDto currencyResponseDto = currencyService.findByCurrencyCode(currencyCode.toUpperCase());
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), currencyResponseDto);
    }
}
