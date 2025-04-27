package com.github.losevskiyfz.listener;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import com.github.losevskiyfz.repository.pool.ConnectionPool;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Properties;

@WebListener
public class ServletContextInitializedListener implements ServletContextListener {
    private static final Logger LOG = LogManager.getLogger(ServletContextInitializedListener.class);
    private final ApplicationContext context = ApplicationContext.getInstance();

    private void initializeApplicationContext() throws SQLException, ClassNotFoundException {
        context.register(PropertiesProvider.class, new PropertiesProvider());
        Class.forName(context.resolve(Properties.class).getProperty("main.datasource.driver"));
        context.register(ConnectionPool.class, new ConnectionPool(
                context.resolve(PropertiesProvider.class).get("main.datasource.url"),
                Integer.parseInt(context.resolve(PropertiesProvider.class).get("main.datasource.pool.size"))
        ));
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            initializeApplicationContext();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
        LOG.info("Application context is initialized");
    }
}