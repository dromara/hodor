/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.actuator.java.core;

import java.lang.reflect.Method;
import org.dromara.hodor.actuator.api.JobExecutionContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.Assert;

/**
 * ScheduledMethods
 *
 * @author tomgs
 * @since 1.0
 */
public class ScheduledMethods {

    public static ScheduledMethodRunnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0
            || method.getParameterCount() == 1, "A method annotated by @Job has at most one parameter");
        if (method.getParameterCount() == 1) {
            Class<?> parameterType = method.getParameterTypes()[0];
            if (!JobExecutionContext.class.isAssignableFrom(parameterType)) {
                throw new IllegalArgumentException("arg must be class JobExecutionContext");
            }
        }

        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod, method.getParameterCount() == 1);
    }

}
