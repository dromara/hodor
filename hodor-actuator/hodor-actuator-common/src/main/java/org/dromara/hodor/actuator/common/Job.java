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

package org.dromara.hodor.actuator.common;

import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;

/**
 * Job instance
 *
 * @author tomgs
 * @since 2021/11/23
 */
public interface Job {

    /**
     * 执行任务
     *
     * @param context 任务上下文
     * @return 任务执行结果
     * @throws JobExecutionException 任务执行异常
     */
    Object execute(JobExecutionContext context) throws JobExecutionException;

    /**
     * 停止任务
     *
     * @param context 任务上下文
     * @throws JobExecutionException 任务停止异常
     */
    void stop(JobExecutionContext context) throws JobExecutionException;

}