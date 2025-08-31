package com.will.currency.exchange.api.repository;

import com.will.currency.exchange.api.exception.DatabaseOperationException;
import com.will.currency.exchange.api.exception.DuplicateEntityException;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

public class ExchangeRateRepository {
    private static final int UNIQUE_CONSTRAINT_VIOLATION_CODE = 19;
    private static final String MESSAGE_UNIQUE_CONSTRAINT_VIOLATION = "Exchange Rate with these codes already exists";
    private static final String SAVE_SQL = """
            INSERT INTO exchange_rate(base_currency_id, target_currency_id, rate)
            VALUES(?, ?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT
                er.id AS exchange_id,
                bc.id AS base_id,
                bc.full_name AS base_name,
                bc.code AS base_code,
                bc.sign AS base_sign,
                tc.id AS target_id,
                tc.full_name AS target_name,
                tc.code AS target_code,
                tc.sign AS target_sign,
                er.rate AS exchange_rate
            FROM exchange_rate er
                JOIN currency bc on bc.id = er.base_currency_id
                JOIN currency tc on tc.id = er.target_currency_id
            """;
    private static final String FIND_ONE_SQL = FIND_ALL_SQL + " WHERE bc.code = ? AND tc.code = ?";
    private static final String UPDATE_SQL = """
            UPDATE exchange_rate
            SET rate = ?
            WHERE id = ?;
            """;

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exchangeRate.setId(generatedKeys.getInt(1));
                }
            }
            return exchangeRate;
        } catch (SQLException err) {
            if (err.getErrorCode() == UNIQUE_CONSTRAINT_VIOLATION_CODE) {
                throw new DuplicateEntityException(MESSAGE_UNIQUE_CONSTRAINT_VIOLATION, err);
            } else {
                throw new DatabaseOperationException(format("Failed to save exchange rate with currencies: %s and %s. %s",
                        exchangeRate.getBaseCurrency().getCode(), exchangeRate.getTargetCurrency().getCode(), err.getMessage()));
            }
        }
    }

    public Optional<ExchangeRate> findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ONE_SQL)) {
            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException err) {
            throw new DatabaseOperationException(format("Failed to get exchange rate with currencies: %s and %s. %s",
                    baseCurrencyCode, targetCurrencyCode, err.getMessage()));
        }
    }

    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ExchangeRate exchangeRate = buildExchangeRate(resultSet);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException err) {
            throw new DatabaseOperationException(format("Failed to get all exchange rates: %s", err.getMessage()));
        }
    }

    public ExchangeRate update(ExchangeRate updatedExchangeRate) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setBigDecimal(1, updatedExchangeRate.getRate());
            preparedStatement.setInt(2, updatedExchangeRate.getId());
            preparedStatement.executeUpdate();
            return updatedExchangeRate;
        } catch (SQLException err) {
            throw new DatabaseOperationException(format("Failed to update exchange rate with id %s: %s",
                    updatedExchangeRate.getId(), err.getMessage()));
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("exchange_id"),
                new Currency(
                        resultSet.getInt("base_id"),
                        resultSet.getString("base_name"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_sign")
                ),
                new Currency(
                        resultSet.getInt("target_id"),
                        resultSet.getString("target_name"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_sign")
                ),
                resultSet.getBigDecimal("exchange_rate")
        );
    }
}
