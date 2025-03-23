package com.github.losevskiyfz.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Properties {
    private final java.util.Properties properties = new java.util.Properties();
    private final Logger logger = Logger.getLogger(Properties.class.getName());

    public Properties(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Properties file is not found");
            }
            properties.load(input);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
