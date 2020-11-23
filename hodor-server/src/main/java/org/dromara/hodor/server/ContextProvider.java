package org.dromara.hodor.server;

import org.springframework.context.ApplicationContext;

/**
 * context provider
 *
 * @author tomgs
 * @since 2020/11/6
 */
public class ContextProvider {

    private static final ContextProvider INSTANCE = new ContextProvider();

    private ApplicationContext applicationContext;

    private ContextProvider() {
    }

    public static ContextProvider getInstance() {
        return INSTANCE;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}
