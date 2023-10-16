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

package org.dromara.hodor.server.executor;

import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.exception.IllegalJobExecuteStateException;

/**
 * MapReduceJobExecutor
 *
 * @author tomgs
 * @since 1.0
 */
public class MapReduceJobExecutor extends CommonJobExecutor {

    @Override
    public void process(HodorJobExecutionContext context) {
        // 调用 split 方法
        final JobKey jobKey = context.getJobKey();
        final JobDesc jobDesc = context.getJobDesc();
        Props jobProps = new Props();
        jobProps.putAll(jobDesc.getJobParameters());
        // 通过在配置指定split任务，hodor.command.split、hodor.command.map、hodor.command.reduce
        final String splitCommand = jobProps.getString("hodor.command.split");
        if (StringUtils.isBlank(splitCommand)) {
            throw new IllegalJobExecuteStateException("The job [{}] hodor.command.split parameter is null", jobKey);
        }

        final String mapCommand = jobProps.getString("hodor.command.map");
        if (StringUtils.isBlank(mapCommand)) {
            throw new IllegalJobExecuteStateException("The job [{}] hodor.command.map parameter is null", jobKey);
        }
        MapReduceJobExecutorManager.getInstance().putMapCommand(jobKey, mapCommand);

        final String reduceCommand = jobProps.getString("hodor.command.reduce");
        if (StringUtils.isNotBlank(reduceCommand)) {
            MapReduceJobExecutorManager.getInstance().putReduceCommand(jobKey, reduceCommand);
        }

        context.setExecCommand(splitCommand);
        context.setShardingId(-1);
        MapReduceJobExecutorManager.getInstance().splitStageDispatch(context);
    }

}
