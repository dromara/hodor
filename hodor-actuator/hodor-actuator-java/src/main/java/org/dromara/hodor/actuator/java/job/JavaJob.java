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

import org.dromara.hodor.actuator.common.Job;
import org.dromara.hodor.actuator.common.JobExecutionContext;
import org.dromara.hodor.actuator.common.JobParameter;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.common.executor.ExecutorManager;
import org.dromara.hodor.actuator.java.core.ScheduledMethodRunnable;

/**
 * JavaJob
 *
 * @author tomgs
 * @since 2021/11/23
 */
public class JavaJob implements Job {

    private final ScheduledMethodRunnable jobRunnable;

    public JavaJob(final ScheduledMethodRunnable jobRunnable) {
        this.jobRunnable = jobRunnable;
    }

    @Override
    public Object execute(JobExecutionContext context) throws JobExecutionException {
        //ScheduledMethodRunnable jobRunnable = jobRegistrar.getJobRunnable(request.getGroupName(), request.getJobName());
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
    public void stop(JobExecutionContext context) {
        JobParameter jobParameter = context.getJobParameter();
        Thread runningThread = ExecutorManager.getInstance().getRunningThread(jobParameter.getRequestId());
        if (runningThread == null) {
            context.getJobLogger().info("not found running job {}#{}", jobParameter.getGroupName(), jobParameter.getJobName());
            return;
        }
        runningThread.interrupt();
    }

}
