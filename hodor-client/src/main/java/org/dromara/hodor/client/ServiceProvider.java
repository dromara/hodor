package org.dromara.hodor.client;

import java.beans.Introspector;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
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

    @SuppressWarnings("unchecked")
    public <T> T registerBean(Class<T> beanClass) {
        Object bean = secondSingletonObjects.get(beanClass);
        if (bean != null) {
            return (T) bean;
        }

        ApplicationContext context = Objects.requireNonNull(this.applicationContext);
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(beanClass);
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);
        bd.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
        beanFactory.registerBeanDefinition(Introspector.decapitalize(beanClass.getSimpleName()), bd);
        return getBean(beanClass);
    }

    public void registerSingleton(Object singletonObject) {
        ApplicationContext context = Objects.requireNonNull(this.applicationContext);
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(Introspector.decapitalize(singletonObject.getClass().getSimpleName()), singletonObject);
    }

}
