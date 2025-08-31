package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.request.ExchangeRateRequestDto;
import com.will.currency.exchange.api.dto.response.ExchangeRateResponseDto;
import com.will.currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

import static com.will.currency.exchange.api.util.Validation.validateCode;
import static com.will.currency.exchange.api.util.Validation.validateExchangeRate;
import static com.will.currency.exchange.api.util.Validation.validatePath;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final String PARAM_RATE = "rate";
    private static final String METHOD_PATCH = "PATCH";
    private static final String SYMBOL_FRONT_SLASH = "/";
    private static final String EMPTY_STRING = "";

    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase(METHOD_PATCH)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo().replace(SYMBOL_FRONT_SLASH, EMPTY_STRING);
        validatePath(pathInfo);
        String baseCurrencyCode = pathInfo.substring(0, 3).toUpperCase();
        String targetCurrencyCode = pathInfo.substring(3).toUpperCase();
        validateCode(baseCurrencyCode);
        validateCode(targetCurrencyCode);

        ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeRateResponseDto);
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto exchangeRateRequest = convertToDto(req);
        validateExchangeRate(exchangeRateRequest);
        ExchangeRateResponseDto updatedExchangeRate = exchangeRateService.update(exchangeRateRequest);
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), updatedExchangeRate);
    }

    private String getRateParameter(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String rate = null;
        String[] split = sb.toString().split("&");
        for (String keyValue : split) {
            String[] s = keyValue.split("=");
            if (s[0].equals(PARAM_RATE)) {
                rate = s[1];
                break;
            }
        }
        return rate;
    }

    private ExchangeRateRequestDto convertToDto(HttpServletRequest req) {
        String pathInfo = req.getPathInfo().replace(SYMBOL_FRONT_SLASH, EMPTY_STRING);
        validatePath(pathInfo);
        String baseCurrencyCode = pathInfo.substring(0, 3);
        String targetCurrencyCode = pathInfo.substring(3);
        String rateStr = getRateParameter(req);
        return new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rateStr);
    }
}
