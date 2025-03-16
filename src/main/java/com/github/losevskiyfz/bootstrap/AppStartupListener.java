package com.github.losevskiyfz.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.service.CurrencyService;
import com.github.losevskiyfz.service.ExchangeRateService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Logger;

@WebListener
public class AppStartupListener implements ServletContextListener {
    private static final Logger logger
            = Logger.getLogger(AppStartupListener.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();

    private void initializeApplicationContext() {
        context.register(
                EntityManagerFactory.class,
                Persistence.createEntityManagerFactory("currencyexchangePU")
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
                ObjectMapper.class,
                new ObjectMapper()
        );
        context.register(
                DataLoader.class,
                new DataLoader()
        );
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initializeApplicationContext();
        context.resolve(DataLoader.class).init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
