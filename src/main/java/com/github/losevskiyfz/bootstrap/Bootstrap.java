package com.github.losevskiyfz.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.config.Properties;
import com.github.losevskiyfz.validator.Validator;
import com.github.losevskiyfz.repository.ExchangeRateRepository;
import com.github.losevskiyfz.repository.pool.ConnectionPool;
import com.github.losevskiyfz.repository.CurrencyRepository;
import com.github.losevskiyfz.service.CurrencyService;
import com.github.losevskiyfz.service.ExchangeRateService;
import com.github.losevskiyfz.service.ExchangeService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebListener
public class Bootstrap implements ServletContextListener {
    private static final Logger logger
            = Logger.getLogger(Bootstrap.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();

    private void initializeApplicationContext() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        context.register(
                Properties.class,
                new Properties()
        );
        Class.forName(context.resolve(Properties.class).getProperty("db.driver"));
        context.register(
                ObjectMapper.class,
                new ObjectMapper()
        );
        context.register(
                ConnectionPool.class,
                new ConnectionPool()
        );
        context.register(
                Validator.class,
                new Validator()
        );
        context.register(
                CurrencyRepository.class,
                new CurrencyRepository()
        );
        context.register(
                ExchangeRateRepository.class,
                new ExchangeRateRepository()
        );
        context.register(
                CurrencyService.class,
                new CurrencyService()
        );
        context.register(
                ExchangeRateService.class,
                new ExchangeRateService()
        );
        context.register(
                ExchangeService.class,
                new ExchangeService()
        );
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            initializeApplicationContext();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("Application context is initialized");
    }
}
