package org.dromara.hodor.server;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationContext;

/**
 * context provider
 *
 * @author tomgs
 * @since 2020/11/6
 */
public class ServiceProvider {

    private static final ServiceProvider INSTANCE = new ServiceProvider();

    private ApplicationContext applicationContext;

    private final Map<Class<?>, Object> secondSingletonObjects = new ConcurrentHashMap<>(16);

    private ServiceProvider() {
    }

    public static ServiceProvider getInstance() {
        return INSTANCE;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        if (this.applicationContext != null) {
            throw new IllegalStateException("application context is already set");
        }
        this.applicationContext = Objects.requireNonNull(applicationContext, "application context is null");
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) secondSingletonObjects.computeIfAbsent(clazz, k -> Objects.requireNonNull(applicationContext).getBean(clazz));
    }

}
