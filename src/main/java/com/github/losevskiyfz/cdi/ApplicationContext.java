package com.github.losevskiyfz.cdi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private final Map<Class<?>, Object> registry = new ConcurrentHashMap<>();
    private static final Logger LOG = LogManager.getLogger(ApplicationContext.class);

    private static ApplicationContext instance;

    private ApplicationContext() {
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            synchronized (ApplicationContext.class) {
                if (instance == null) {
                    instance = new ApplicationContext();
                }
            }
        }
        return instance;
    }

    public <T> void register(Class<T> clazz, Object instance) {
        LOG.info("Registering {} with {}", clazz.getName(), instance.getClass().getName());
        registry.put(clazz, instance);
    }

    public <T> T resolve(Class<T> clazz) {
        Object instance = registry.get(clazz);
        if (instance == null) {
            throw new IllegalStateException();
        }
        LOG.info("Resolving {} with {}", clazz.getName(), instance.getClass().getName());
        return clazz.cast(instance);
    }
}