package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.entity.Currency;
import java.util.List;
import java.util.logging.Logger;

public class CurrencyRepositoryImpl implements CurrencyRepository {
    private static final Logger LOG = Logger.getLogger(CurrencyRepositoryImpl.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final JdbcTemplate jdbcTemplate = context.resolve(JdbcTemplate.class);

    private static final String CURRENCY_FIND_ALL = "SELECT * FROM Currencies";
    private static final String CURRENCY_FIND_BY_CODE= "SELECT * FROM Currencies WHERE Code = ?";

    @Override
    public List<Currency> findAll() {
        LOG.info("Finding all currencies.");
        return jdbcTemplate.queryForList(CURRENCY_FIND_ALL, Currency.class);
    }

    @Override
    public Currency findByCode(String code) {
        LOG.info("Finding currency by code: " + code);
        return jdbcTemplate.queryForObject(CURRENCY_FIND_BY_CODE, Currency.class, code);
    }
}
