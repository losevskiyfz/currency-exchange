package com.github.losevskiyfz.cdi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ApplicationContext {
    private final Map<Class<?>, Object> registry = new ConcurrentHashMap<>();
    private static final Logger logger
            = Logger.getLogger(ApplicationContext.class.getName());

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
        logger.fine("Registering " + clazz.getName() + " with " + instance.getClass().getName());
        registry.put(clazz, instance);
    }

    public <T> T resolve(Class<T> clazz) {
        Object instance = registry.get(clazz);
        if (instance == null) {
            throw new IllegalStateException("No registered instance for " + clazz.getName());
        }
        logger.fine("Resolving " + clazz.getName() + " with " + instance.getClass().getName());
        return clazz.cast(instance);
    }
}
