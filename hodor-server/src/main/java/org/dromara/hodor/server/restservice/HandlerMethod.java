package org.dromara.hodor.server.restservice;

import java.lang.reflect.Method;
import lombok.Data;

/**
 * simple handler method
 *
 * @author tomgs
 */
@Data
public class HandlerMethod {

    private Object bean;

    private Method method;

    private MethodParameter parameter;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object invoke(Object... args) throws Exception {
        return this.method.invoke(bean, args);
    }

}
