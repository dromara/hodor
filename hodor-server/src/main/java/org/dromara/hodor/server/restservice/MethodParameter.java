package org.dromara.hodor.server.restservice;

import lombok.Data;

@Data
public class MethodParameter {

    private String parameterName;

    private Class<?> parameterType;

}
