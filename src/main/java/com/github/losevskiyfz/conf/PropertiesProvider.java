package com.github.losevskiyfz.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesProvider {
    private static final Logger LOG = LogManager.getLogger(PropertiesProvider.class);
    private final Properties properties;

    public PropertiesProvider() {
        this.properties = loadProperties();
    }

    private Properties loadProperties() {
        LOG.info("Loading application.properties to the project.");
        try (InputStream input = PropertiesProvider.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties file not found in classpath");
            }
            Properties props = new Properties();
            props.load(input);
            return props;
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

    public String get(String property) {
        return properties.getProperty(property);
    }
}
