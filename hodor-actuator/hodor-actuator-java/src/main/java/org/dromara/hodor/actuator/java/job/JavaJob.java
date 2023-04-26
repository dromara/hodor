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

package org.dromara.hodor.actuator.java.job;

import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.JobExecutionContext;
import org.dromara.hodor.actuator.api.JobParameter;
import org.dromara.hodor.actuator.api.ExecutableJob;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.java.core.ScheduledMethodRunnable;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;

/**
 * JavaJob
 *
 * @author tomgs
 * @since 2021/11/23
 */
public class JavaJob implements ExecutableJob {

    private final ScheduledMethodRunnable jobRunnable;

    public JavaJob(final ScheduledMethodRunnable jobRunnable) {
        this.jobRunnable = jobRunnable;
    }

    @Override
    public Object execute(ExecutableJobContext jobContext) throws JobExecutionException {
        final Logger jobLogger = jobContext.getJobLogger().getLogger();
        final JobExecuteRequest request = jobContext.getExecuteRequest();
        final JobParameter jobParameter = new JobParameter(request.getGroupName(), request.getJobName(), request.getRequestId(),
            request.getJobParameters(), request.getShardId(), request.getShardName());
        final JobExecutionContext context = new JobExecutionContext(jobLogger, jobParameter);
        JobParameter jobInfo = context.getJobParameter();
        if (jobRunnable == null) {
            throw new IllegalArgumentException(String.format("not found job %s_%s.", jobInfo.getGroupName(), jobInfo.getJobName()));
        }
        if (jobRunnable.isHasArg()) {
            jobRunnable.setArgs(context);
        }

        Object result;
        try {
            jobRunnable.run();
            result = jobRunnable.getResult();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {
            jobRunnable.refresh();
        }
        return result;
    }

    @Override
    public void stop(ExecutableJobContext jobContext) {
        Thread runningThread = jobContext.getCurrentThread();
        if (runningThread == null) {
            jobContext.getJobLogger().getLogger().info("not found running job {}", jobContext.getJobKey());
            return;
        }
        runningThread.interrupt();
        jobContext.setExecuteStatus(JobExecuteStatus.KILLED);
    }

}
