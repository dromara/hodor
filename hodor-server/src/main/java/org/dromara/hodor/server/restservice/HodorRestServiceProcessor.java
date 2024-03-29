package org.dromara.hodor.server.restservice;

import cn.hutool.core.util.ModifierUtil;
import java.lang.reflect.Method;
import org.dromara.hodor.common.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;

/**
 * get hodor rest service
 *
 * @author tomgs
 */
public class HodorRestServiceProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        HodorRestService hodorRestServiceAnnotation = beanClass.getDeclaredAnnotation(HodorRestService.class);
        if (hodorRestServiceAnnotation == null) {
            return bean;
        }
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (!ModifierUtil.isPublic(declaredMethod)) {
                continue;
            }
            RestMethod restMethod = declaredMethod.getDeclaredAnnotation(RestMethod.class);
            String path;
            if (restMethod == null || StringUtils.isBlank(restMethod.value())) {
                path = beanName + "/" + declaredMethod.getName();
            } else {
                path = beanName + "/" + restMethod.value();
            }
            RestServiceRequestHandler.REST_SERVICES.put(path, new HandlerMethod(bean, declaredMethod));
        }
        return bean;
    }

}
