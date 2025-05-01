package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.entity.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {
    private static final Logger LOG = Logger.getLogger(ExchangeRateRepositoryImpl.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final JdbcTemplate jdbcTemplate = context.resolve(JdbcTemplate.class);

    @Override
    public List<ExchangeRate> findAll() {
        LOG.info("Finding all exchange rates");
        return jdbcTemplate.queryForList(EXCHANGE_RATE_FIND_ALL, new ExchangeRateRowMapper());
    }

    @Override
    public ExchangeRate findBySourceAndTargetCode(String sourceCode, String targetCode) {
        LOG.info("Search exchange rate by source and target codes");
        return jdbcTemplate.queryForObject(
                EXCHANGE_RATE_FIND_BY_SOURCE_AND_TARGET_CODE,
                new ExchangeRateRowMapper(),
                sourceCode,
                targetCode
        );
    }

    private static final String EXCHANGE_RATE_FIND_ALL = """
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

    private static final String EXCHANGE_RATE_FIND_BY_SOURCE_AND_TARGET_CODE = """
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

    private static class ExchangeRateRowMapper implements RowMapper<ExchangeRate> {
        @Override
        public ExchangeRate mapRow(ResultSet resultSet) throws SQLException {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setId(resultSet.getInt("ID"));
            exchangeRate.setBaseCurrency(
                    Currency.builder()
                            .id(resultSet.getInt("baseCurrencyId"))
                            .sign(resultSet.getString("baseCurrencySign"))
                            .code(resultSet.getString("baseCurrencyCode"))
                            .fullName(resultSet.getString("baseCurrencyName"))
                            .build()
            );
            exchangeRate.setTargetCurrency(
                    Currency.builder()
                            .id(resultSet.getInt("targetCurrencyId"))
                            .sign(resultSet.getString("targetCurrencySign"))
                            .code(resultSet.getString("targetCurrencyCode"))
                            .fullName(resultSet.getString("targetCurrencyName"))
                            .build()
            );
            exchangeRate.setRate(resultSet.getBigDecimal("Rate"));
            return exchangeRate;
        }
    }
}
