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

import java.util.Set;
import org.dromara.hodor.model.job.JobInstance;
import org.dromara.hodor.model.job.JobKey;

/**
 * JobRegistrar
 *
 * @author tomgs
 * @since 2021/11/23
 */
public interface JobRegistrar {

    /**
     * 任务注册
     *
     * @throws Exception 任务注册异常
     */
    void registerJobs() throws Exception;

    /**
     * register job
     *
     * @param jobInstance job instance
     */
    void registerJob(JobInstance jobInstance);

    /**
     * get runnable job by job key
     *
     * @param jobKey job key
     * @return runnable job
     */
    JobInstance getJob(JobKey jobKey);

    /**
     * get group name set
     *
     * @return groupName set
     */
    Set<String> getGroupNames();

    /**
     * clear register jobs cache
     */
    void clear();
}
