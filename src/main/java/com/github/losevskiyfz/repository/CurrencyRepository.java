package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.repository.pool.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class CurrencyRepository {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ConnectionPool connectionPool = context.resolve(ConnectionPool.class);
    private final Logger logger = Logger.getLogger(CurrencyRepository.class.getName());

    public List<Currency> findAll() throws SQLException, InterruptedException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.borrowConnection();
            statement = connection.createStatement();
            String query = "SELECT * FROM Currencies";
            resultSet = statement.executeQuery(query);
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                Currency currency =
                        Currency.builder()
                                .id(resultSet.getLong("ID"))
                                .fullName(resultSet.getString("FullName"))
                                .code(resultSet.getString("Code"))
                                .sign(resultSet.getString("Sign"))
                                .build();
                currencies.add(currency);
            }
            return currencies;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connectionPool.returnConnection(connection);
                }
            } catch (SQLException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public Optional<Currency> findByCode(String code) throws SQLException, InterruptedException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.borrowConnection();
            String query = "SELECT * FROM Currencies WHERE Code = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, code);
            resultSet = statement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency =
                        Currency.builder()
                                .id(resultSet.getLong("ID"))
                                .fullName(resultSet.getString("FullName"))
                                .code(resultSet.getString("Code"))
                                .sign(resultSet.getString("Sign"))
                                .build();
            }
            return Optional.ofNullable(currency);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connectionPool.returnConnection(connection);
                }
            } catch (SQLException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public Currency save(Currency currency) throws SQLException, InterruptedException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.borrowConnection();
            String query = "INSERT INTO Currencies(FullName, Code, Sign) VALUES(?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, currency.getFullName());
            statement.setString(2, currency.getCode());
            statement.setString(3, currency.getSign());
            int affectedRows = statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return Currency.builder()
                        .id(resultSet.getLong("last_insert_rowId()"))
                        .fullName(currency.getFullName())
                        .code(currency.getCode())
                        .sign(currency.getSign())
                        .build();
            } else {
                throw new SQLException();
            }

        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connectionPool.returnConnection(connection);
                }
            } catch (SQLException e) {
                logger.severe(e.getMessage());
            }
        }
    }
}
