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

import org.dromara.hodor.actuator.common.JobExecutionContext;
import org.dromara.hodor.actuator.common.JobParameter;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.actuator.common.core.ExecutableJob;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.java.core.ScheduledMethodRunnable;
import org.dromara.hodor.model.enums.JobExecuteStatus;

/**
 * JavaJob
 *
 * @author tomgs
 * @since 2021/11/23
 */
public class JavaJob implements JobRunnable {

    private final ScheduledMethodRunnable jobRunnable;

    public JavaJob(final ScheduledMethodRunnable jobRunnable) {
        this.jobRunnable = jobRunnable;
    }

    @Override
    public Object execute(ExecutableJob job) throws JobExecutionException {
        JobExecutionContext context = job.getExecutionContext();
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
    public void stop(ExecutableJob job) {
        Thread runningThread = job.getCurrentThread();
        if (runningThread == null) {
            job.getJobLogger().info("not found running job {}", job.getJobKey());
            return;
        }
        runningThread.interrupt();
        job.setExecuteStatus(JobExecuteStatus.KILLED);
    }

}
