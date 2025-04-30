package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.entity.Currency;
import com.github.losevskiyfz.exception.IdNotGeneratedException;
import com.github.losevskiyfz.exception.UniqueConstraintViolationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class CurrencyRepositoryImpl implements CurrencyRepository {
    private static final Logger LOG = Logger.getLogger(CurrencyRepositoryImpl.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final JdbcTemplate jdbcTemplate = context.resolve(JdbcTemplate.class);

    private static final String CURRENCY_FIND_ALL = "SELECT * FROM Currencies";
    private static final String CURRENCY_FIND_BY_CODE = "SELECT * FROM Currencies WHERE Code = ?";
    private static final String CURRENCY_INSERT = "INSERT INTO Currencies(FullName, Code, Sign) VALUES(?, ?, ?)";

    @Override
    public List<Currency> findAll() {
        LOG.info("Finding all currencies.");
        return jdbcTemplate.queryForList(
                CURRENCY_FIND_ALL,
                new CurrencyRowMapper()
        );
    }

    @Override
    public Currency findByCode(String code) {
        LOG.info("Finding currency by code: " + code);
        return jdbcTemplate.queryForObject(
                CURRENCY_FIND_BY_CODE,
                new CurrencyRowMapper(),
                code
        );
    }

    @Override
    public Currency save(Currency currency) {
        LOG.info("Saving currency: " + currency);
        try {
            Integer id = jdbcTemplate.update(CURRENCY_INSERT, currency.getFullName(), currency.getCode(), currency.getSign());
            if (id == null) {
                throw new IdNotGeneratedException("Couldn't generate id for currency: " + currency);
            }
            return Currency.builder()
                    .id(id)
                    .fullName(currency.getFullName())
                    .code(currency.getCode())
                    .sign(currency.getSign())
                    .build();
        } catch (UniqueConstraintViolationException e) {
            throw new UniqueConstraintViolationException("Currency with code " + currency.getCode() + " already exists");
        }
    }

    private static class CurrencyRowMapper implements RowMapper<Currency> {
        @Override
        public Currency mapRow(ResultSet resultSet) throws SQLException {
            Currency currency = new Currency();
            currency.setId(resultSet.getInt("ID"));
            currency.setFullName(resultSet.getString("FullName"));
            currency.setCode(resultSet.getString("Code"));
            currency.setSign(resultSet.getString("Sign"));
            return currency;
        }
    }
}
