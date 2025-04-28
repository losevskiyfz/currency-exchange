package com.github.losevskiyfz.cdi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ApplicationContext {
    private final Map<Class<?>, Object> registry = new ConcurrentHashMap<>();
    private static final Logger LOG = Logger.getLogger(ApplicationContext.class.getName());

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
        LOG.info(String.format("Registering %s with %s", clazz.getName(), instance.getClass().getName()));
        registry.put(clazz, instance);
    }

    public <T> T resolve(Class<T> clazz) {
        Object instance = registry.get(clazz);
        if (instance == null) {
            throw new IllegalStateException();
        }
        LOG.info(String.format("Resolving %s with %s", clazz.getName(), instance.getClass().getName()));
        return clazz.cast(instance);
    }
}