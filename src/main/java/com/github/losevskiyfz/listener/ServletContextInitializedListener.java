package com.github.losevskiyfz.listener;

import com.github.losevskiyfz.cdi.ApplicationContext;
import com.github.losevskiyfz.conf.PropertiesProvider;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebListener
public class ServletContextInitializedListener implements ServletContextListener {
    private static final Logger LOG = LogManager.getLogger(ServletContextInitializedListener.class);
    private final ApplicationContext context = ApplicationContext.getInstance();

    private void initializeApplicationContext() {
        context.register(PropertiesProvider.class, new PropertiesProvider());
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