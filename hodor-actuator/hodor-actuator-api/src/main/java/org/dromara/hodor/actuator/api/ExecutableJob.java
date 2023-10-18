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

package org.dromara.hodor.actuator.api;

import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.exceptions.JobExecutionException;
import org.dromara.hodor.model.enums.JobExecuteStatus;

/**
 * ExecutableJob
 *
 * @author tomgs
 * @since 1.0
 */
public interface ExecutableJob {

    /**
     * 执行任务
     *
     * @param executableJobContext 待执行任务
     * @return 任务执行结果
     * @throws JobExecutionException 任务执行异常
     */
    Object execute(ExecutableJobContext executableJobContext) throws Exception;

    /**
     * 停止任务
     *
     * @param executableJobContext 待执行任务
     * @throws JobExecutionException 任务停止异常
     */
    void stop(ExecutableJobContext executableJobContext) throws Exception;

    /**
     * 获取任务状态
     *
     * @param executableJobContext 待执行任务
     * @return JobExecuteStatus 任务状态
     * @throws JobExecutionException 任务状态异常
     */
    default JobExecuteStatus status(ExecutableJobContext executableJobContext) throws Exception {
        if (executableJobContext == null) {
            return JobExecuteStatus.FINISHED;
        }
        return executableJobContext.getExecuteStatus();
    }

}
