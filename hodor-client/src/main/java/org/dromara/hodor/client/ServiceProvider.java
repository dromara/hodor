package org.dromara.hodor.client;

import java.util.Objects;
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

    public <T> T getBean(Class<T> clazz) {
        return Objects.requireNonNull(applicationContext).getBean(clazz);
    }

}
