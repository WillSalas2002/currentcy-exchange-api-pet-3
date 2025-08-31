package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.request.CurrencyRequestDto;
import com.will.currency.exchange.api.dto.response.CurrencyResponseDto;
import com.will.currency.exchange.api.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.will.currency.exchange.api.util.Validation.validateCode;
import static com.will.currency.exchange.api.util.Validation.validateName;
import static com.will.currency.exchange.api.util.Validation.validateSign;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final String PARAM_NAME = "name";
    private static final String PARAM_CODE = "code";
    private static final String PARAM_SIGN = "sign";

    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> currencies = currencyService.findAll();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyRequestDto currencyRequest = convertToDto(req);
        CurrencyResponseDto currencyResponseDto = currencyService.save(currencyRequest);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), currencyResponseDto);
    }

    private static CurrencyRequestDto convertToDto(HttpServletRequest req) {
        String code = req.getParameter(PARAM_CODE);
        String name = req.getParameter(PARAM_NAME);
        String sign = req.getParameter(PARAM_SIGN);

        validateCode(code);
        validateName(name);
        validateSign(sign);

        return new CurrencyRequestDto(code.trim().toUpperCase(), name.trim(), sign.trim());
    }
}
