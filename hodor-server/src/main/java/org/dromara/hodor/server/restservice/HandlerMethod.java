package org.dromara.hodor.server.restservice;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
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

    private MethodParameter[] parameter;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.initMethodParameter();
    }

    private void initMethodParameter() {
        this.parameter = new MethodParameter[this.method.getParameterCount()];
        for (int i = 0; i < this.method.getParameters().length; i++) {
            Parameter rawMethodParameter = method.getParameters()[i];
            MethodParameter methodParameter = new MethodParameter();
            methodParameter.setParameterName(rawMethodParameter.getName());
            methodParameter.setParameterType(rawMethodParameter.getType());
            methodParameter.setParameterizedType(rawMethodParameter.getParameterizedType());

            this.parameter[i] = methodParameter;
        }
    }

    public Object invoke(Object... args) throws Exception {
        return this.method.invoke(bean, args);
    }

    @Data
    public static class MethodParameter {

        private String parameterName;

        private Class<?> parameterType;

        private Type parameterizedType;

    }

}
