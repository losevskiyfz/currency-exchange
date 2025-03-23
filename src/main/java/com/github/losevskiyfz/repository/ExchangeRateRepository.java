package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.entity.ExchangeRate;
import com.github.losevskiyfz.repository.pool.ConnectionPool;
import jakarta.validation.ConstraintViolationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ExchangeRateRepository {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final ConnectionPool connectionPool = context.resolve(ConnectionPool.class);
    private final Logger logger = Logger.getLogger(ExchangeRateRepository.class.getName());

    public List<ExchangeRate> findAll() throws SQLException, InterruptedException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.borrowConnection();
            String query = """
                    SELECT er.ID,
                           bc.ID baseCurrencyId,
                           bc.FullName baseCurrencyName,
                           bc.Code baseCurrencyCode,
                           bc.Sign baseCurrencySign,
                           tc.ID targetCurrencyId,
                           tc.FullName targetCurrencyName,
                           tc.Code targetCurrencyCode,
                           tc.Sign targetCurrencySign,
                           er.Rate
                           FROM ExchangeRates er
                    JOIN Currencies bc ON er.BaseCurrencyId = bc.ID
                    JOIN Currencies tc ON er.TargetCurrencyId = tc.ID
                    """;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                Currency bc = Currency.builder()
                        .id(resultSet.getLong("baseCurrencyId"))
                        .fullName(resultSet.getString("baseCurrencyName"))
                        .sign(resultSet.getString("baseCurrencySign"))
                        .code(resultSet.getString("baseCurrencyCode"))
                        .build();
                Currency tc = Currency.builder()
                        .id(resultSet.getLong("targetCurrencyId"))
                        .fullName(resultSet.getString("targetCurrencyName"))
                        .sign(resultSet.getString("targetCurrencySign"))
                        .code(resultSet.getString("targetCurrencyCode"))
                        .build();
                ExchangeRate er = ExchangeRate.builder()
                        .id(resultSet.getLong("ID"))
                        .baseCurrency(bc)
                        .targetCurrency(tc)
                        .rate(resultSet.getBigDecimal("Rate"))
                        .build();
                exchangeRates.add(er);
            }
            return exchangeRates;
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

    public Optional<ExchangeRate> findByBaseCodeAndTargetCode(String baseCode, String targetCode) throws SQLException, InterruptedException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.borrowConnection();
            String query = """
                    SELECT er.ID,
                           bc.ID baseCurrencyId,
                           bc.FullName baseCurrencyName,
                           bc.Code baseCurrencyCode,
                           bc.Sign baseCurrencySign,
                           tc.ID targetCurrencyId,
                           tc.FullName targetCurrencyName,
                           tc.Code targetCurrencyCode,
                           tc.Sign targetCurrencySign,
                           er.Rate
                    FROM ExchangeRates er
                    JOIN Currencies bc ON er.BaseCurrencyId = bc.ID
                    JOIN Currencies tc ON er.TargetCurrencyId = tc.ID
                    WHERE bc.Code = ?
                    AND tc.Code = ?
                    """;
            statement = connection.prepareStatement(query);
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                Currency bc = Currency.builder()
                        .id(resultSet.getLong("baseCurrencyId"))
                        .fullName(resultSet.getString("baseCurrencyName"))
                        .sign(resultSet.getString("baseCurrencySign"))
                        .code(resultSet.getString("baseCurrencyCode"))
                        .build();
                Currency tc = Currency.builder()
                        .id(resultSet.getLong("targetCurrencyId"))
                        .fullName(resultSet.getString("targetCurrencyName"))
                        .sign(resultSet.getString("targetCurrencySign"))
                        .code(resultSet.getString("targetCurrencyCode"))
                        .build();
                exchangeRate = ExchangeRate.builder()
                        .id(resultSet.getLong("ID"))
                        .baseCurrency(bc)
                        .targetCurrency(tc)
                        .rate(resultSet.getBigDecimal("Rate"))
                        .build();
            }
            return Optional.ofNullable(exchangeRate);
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

    public ExchangeRate save(ExchangeRate exchangeRate) throws SQLException, InterruptedException {
        Connection connection = null;
        PreparedStatement insertStatement = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;
        String insertQry = """
                INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
                SELECT base.ID, target.ID, ?
                FROM Currencies base
                JOIN Currencies target ON target.Code = ?
                WHERE base.Code = ?;
                """;
        String selectCurrencyQry = """
                SELECT * FROM Currencies WHERE Code = ?
                """;
        try {
            connection = connectionPool.borrowConnection();
            connection.setAutoCommit(false);
            insertStatement = connection.prepareStatement(insertQry, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setBigDecimal(1, exchangeRate.getRate());
            insertStatement.setString(2, exchangeRate.getTargetCurrency().getCode());
            insertStatement.setString(3, exchangeRate.getBaseCurrency().getCode());
            try {
                int affectedRows = insertStatement.executeUpdate();
            } catch (SQLException e) {
                if (e.getSQLState().equals("23505")) {
                    throw new ConstraintViolationException(Collections.emptySet());
                } else {
                    throw e;
                }
            }
            resultSet = insertStatement.getGeneratedKeys();
            Long id = null;
            if (resultSet.next()) {
                id = resultSet.getLong("last_insert_rowid()");
            } else {
                throw new SQLException();
            }
            selectStatement = connection.prepareStatement(selectCurrencyQry);
            selectStatement.setString(1, exchangeRate.getBaseCurrency().getCode());
            resultSet = selectStatement.executeQuery();
            Currency baseCurrency = null;
            if (resultSet.next()) {
                baseCurrency = Currency.builder()
                        .id(resultSet.getLong("ID"))
                        .code(resultSet.getString("Code"))
                        .fullName(resultSet.getString("FullName"))
                        .sign(resultSet.getString("Sign"))
                        .build();
            }
            selectStatement.setString(1, exchangeRate.getTargetCurrency().getCode());
            resultSet = selectStatement.executeQuery();
            Currency targetCurrency = null;
            if (resultSet.next()) {
                targetCurrency = Currency.builder()
                        .id(resultSet.getLong("ID"))
                        .code(resultSet.getString("Code"))
                        .fullName(resultSet.getString("FullName"))
                        .sign(resultSet.getString("Sign"))
                        .build();
            }
            if (baseCurrency == null || targetCurrency == null) {
                throw new IllegalArgumentException();
            }
            connection.commit();
            return ExchangeRate.builder()
                    .id(id)
                    .baseCurrency(baseCurrency)
                    .targetCurrency(targetCurrency)
                    .rate(exchangeRate.getRate())
                    .build();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (insertStatement != null) {
                insertStatement.close();
            }
            if (selectStatement != null) {
                selectStatement.close();
            }
            if (connection != null) {
                connection.rollback();
                connectionPool.returnConnection(connection);
            }
        }
    }

    public ExchangeRate update(ExchangeRate exchangeRate) throws SQLException, InterruptedException {
        Connection connection = null;
        PreparedStatement updateStatement = null;
        PreparedStatement selectCurStatement = null;
        PreparedStatement selectExchRateStatement = null;
        ResultSet resultSet = null;
        String updateQry = """
                UPDATE ExchangeRates
                SET Rate = ?
                WHERE BaseCurrencyId IN (SELECT id FROM Currencies WHERE code = ?)
                AND TargetCurrencyId IN (SELECT id FROM Currencies WHERE code = ?)
                """;
        String selectCurrencyQry = """
                SELECT * FROM Currencies WHERE Code = ?
                """;
        String selectExchangeRateQry = """
                SELECT ID FROM ExchangeRates
                WHERE BaseCurrencyId IN (SELECT id FROM Currencies WHERE code = ?)
                AND TargetCurrencyId IN (SELECT id FROM Currencies WHERE code = ?)
                """;
        try {
            connection = connectionPool.borrowConnection();
            connection.setAutoCommit(false);
            updateStatement = connection.prepareStatement(updateQry);
            updateStatement.setBigDecimal(1, exchangeRate.getRate());
            updateStatement.setString(2, exchangeRate.getBaseCurrency().getCode());
            updateStatement.setString(3, exchangeRate.getTargetCurrency().getCode());
            int affectedRows = updateStatement.executeUpdate();
            selectCurStatement = connection.prepareStatement(selectCurrencyQry);
            selectCurStatement.setString(1, exchangeRate.getBaseCurrency().getCode());
            resultSet = selectCurStatement.executeQuery();
            Currency baseCurrency = null;
            if (resultSet.next()) {
                baseCurrency = Currency.builder()
                        .id(resultSet.getLong("ID"))
                        .code(resultSet.getString("Code"))
                        .fullName(resultSet.getString("FullName"))
                        .sign(resultSet.getString("Sign"))
                        .build();
            }
            selectCurStatement.setString(1, exchangeRate.getTargetCurrency().getCode());
            resultSet = selectCurStatement.executeQuery();
            Currency targetCurrency = null;
            if (resultSet.next()) {
                targetCurrency = Currency.builder()
                        .id(resultSet.getLong("ID"))
                        .code(resultSet.getString("Code"))
                        .fullName(resultSet.getString("FullName"))
                        .sign(resultSet.getString("Sign"))
                        .build();
            }
            if (baseCurrency == null || targetCurrency == null) {
                throw new IllegalArgumentException();
            }
            selectExchRateStatement = connection.prepareStatement(selectExchangeRateQry);
            selectExchRateStatement.setString(1, exchangeRate.getBaseCurrency().getCode());
            selectExchRateStatement.setString(2, exchangeRate.getTargetCurrency().getCode());
            resultSet = selectExchRateStatement.executeQuery();
            Long id;
            if (resultSet.next()) {
                id = resultSet.getLong("ID");
            } else {
                throw new SQLException();
            }
            connection.commit();
            return ExchangeRate.builder()
                    .id(id)
                    .baseCurrency(baseCurrency)
                    .targetCurrency(targetCurrency)
                    .rate(exchangeRate.getRate())
                    .build();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (updateStatement != null) {
                updateStatement.close();
            }
            if (selectCurStatement != null) {
                selectCurStatement.close();
            }
            if (connection != null) {
                connection.rollback();
                connectionPool.returnConnection(connection);
            }
        }
    }
}
