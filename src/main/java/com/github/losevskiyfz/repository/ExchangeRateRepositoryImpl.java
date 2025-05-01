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
    private final CurrencyRepository currencyRepository = context.resolve(CurrencyRepository.class);

    @Override
    public List<ExchangeRate> findAll() {
        LOG.info("Finding all exchange rates");
        return jdbcTemplate.queryForList(EXCHANGE_RATE_FIND_ALL, new ExchangeRateRowMapper());
    }

    @Override
    public ExchangeRate findBySourceAndTargetCode(String baseCode, String targetCode) {
        LOG.info("Search exchange rate by source and target codes");
        return jdbcTemplate.queryForObject(
                EXCHANGE_RATE_FIND_BY_SOURCE_AND_TARGET_CODE,
                new ExchangeRateRowMapper(),
                baseCode,
                targetCode
        );
    }

    // ! IMPORTANT: Since we can't update, delete, patch Currency - it's immutable, we can't encounter
    // race conditions related to querying irrelevant Currency value.
    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        Currency baseCurrency = currencyRepository.findByCode(exchangeRate.getBaseCurrency().getCode());
        Currency targetCurrency = currencyRepository.findByCode(exchangeRate.getTargetCurrency().getCode());
        Integer id = jdbcTemplate.update(
                EXCHANGE_RATE_INSERT,
                exchangeRate.getRate(),
                baseCurrency.getCode(),
                targetCurrency.getCode()
        );
        return ExchangeRate.builder()
                .id(id)
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(exchangeRate.getRate())
                .build();
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

    private static final String EXCHANGE_RATE_INSERT = """
            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
                SELECT base.ID, target.ID, ?
                FROM Currencies target
                JOIN Currencies base ON base.Code = ?
                WHERE target.Code = ?;
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
