package org.dromara.hodor.server.restservice;

import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * get hodor rest service
 *
 * @author tomgs
 */
public class HodorRestServiceProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        HodorRestService hodorRestServiceAnnotation = beanClass.getDeclaredAnnotation(HodorRestService.class);
        if (hodorRestServiceAnnotation == null) {
            return bean;
        }
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            RestServiceRequestHandler.REST_SERVICES.put(beanName + "/" + declaredMethod.getName(), new HandlerMethod(bean, declaredMethod));
        }
        return bean;
    }

}
