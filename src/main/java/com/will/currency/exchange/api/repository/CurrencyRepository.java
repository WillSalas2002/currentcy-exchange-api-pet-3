package com.will.currency.exchange.api.repository;

import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository implements Repository<Currency> {

    private final String SAVE_SQL = """
            INSERT INTO Currency(full_name, code, sign)
            VALUES(?, ?, ?);
            """;
    private final String FIND_ALL_SQL = """
            SELECT id, full_name, code, sign
            FROM Currency;
            """;
    private final String UPDATE_SQL = """
            UPDATE Currency
            SET full_name = ?, code = ?, sign = ?
            WHERE id = ?;
            """;
    private final String DELETE_SQL = """
            DELETE FROM Currency
            WHERE id = ?;
            """;

    @Override
    public Currency save(Currency currency) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getFullName());
            preparedStatement.setString(2, currency.getCode());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
            return currency;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Currency> findAll() {

        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                Currency currency = buildCurrency(resultSet);
                currencies.add(currency);
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Currency update(Currency updatedCurrency) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, updatedCurrency.getFullName());
            preparedStatement.setString(2, updatedCurrency.getCode());
            preparedStatement.setString(3, updatedCurrency.getSign());
            preparedStatement.setInt(4, updatedCurrency.getId());
            preparedStatement.executeUpdate();
            return updatedCurrency;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Currency entity) {

    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }
}