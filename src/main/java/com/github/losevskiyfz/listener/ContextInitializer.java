package com.github.losevskiyfz.listener;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.repository.CurrencyRepository;
import com.github.losevskiyfz.repository.CurrencyRepositoryImpl;
import com.github.losevskiyfz.repository.JdbcTemplate;
import com.github.losevskiyfz.repository.pool.ConnectionPool;
import com.github.losevskiyfz.service.CurrencyService;
import com.github.losevskiyfz.service.CurrencyServiceImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.SQLException;
import java.util.logging.Logger;

@WebListener
public class ContextInitializer implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(ContextInitializer.class.getName());
    private final ApplicationContext context = ApplicationContext.getInstance();

    private void initializeApplicationContext() throws SQLException, ClassNotFoundException {
        Class.forName(PropertiesProvider.get("main.datasource.driver"));
        context.register(ConnectionPool.class, new ConnectionPool(
                PropertiesProvider.get("main.datasource.url"),
                Integer.parseInt(PropertiesProvider.get("main.datasource.pool.size"))
        ));
        context.register(
                JdbcTemplate.class,
                new JdbcTemplate()
        );
        context.register(
                CurrencyRepository.class,
                new CurrencyRepositoryImpl()
        );
        context.register(
                CurrencyService.class,
                new CurrencyServiceImpl()
        );
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            initializeApplicationContext();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            throw new RuntimeException(e);
        }
        LOG.info("Application context is initialized");
    }
}