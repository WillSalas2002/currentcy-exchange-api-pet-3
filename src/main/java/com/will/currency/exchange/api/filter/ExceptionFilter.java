package com.will.currency.exchange.api.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.dto.response.ErrorResponseDto;
import com.will.currency.exchange.api.exception.BadRequestException;
import com.will.currency.exchange.api.exception.CurrencyConversionException;
import com.will.currency.exchange.api.exception.DatabaseOperationException;
import com.will.currency.exchange.api.exception.DuplicateEntityException;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebFilter("/*")
public class ExceptionFilter extends HttpFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            super.doFilter(req, res, chain);
        }
        catch (DuplicateEntityException e) {
            writeErrorResponse(res, SC_CONFLICT, e);
        }
        catch (BadRequestException e) {
            writeErrorResponse(res, SC_BAD_REQUEST, e);
        }
        catch (CurrencyConversionException | NoSuchEntityException e) {
            writeErrorResponse(res, SC_NOT_FOUND, e);
        }
        catch (DatabaseOperationException e) {
            writeErrorResponse(res, SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int errorCode, RuntimeException e) throws IOException {
        response.setStatus(errorCode);
        objectMapper.writeValue(response.getWriter(), new ErrorResponseDto(
                e.getMessage()
        ));
    }
}
