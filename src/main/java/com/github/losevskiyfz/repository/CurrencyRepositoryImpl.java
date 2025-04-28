package com.github.losevskiyfz.repository;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.entity.Currency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CurrencyRepositoryImpl implements CurrencyRepository {
    private static final Logger LOG = LogManager.getLogger(CurrencyRepositoryImpl.class);
    private final ApplicationContext context = ApplicationContext.getInstance();
    private final JdbcTemplate jdbcTemplate = context.resolve(JdbcTemplate.class);

    private static final String CURRENCY_FIND_ALL = "SELECT * FROM Currencies";

    @Override
    public List<Currency> findAll() {
        LOG.info("Finding all currencies.");
        return jdbcTemplate.queryForList(CURRENCY_FIND_ALL, Currency.class);
    }
}
