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

import java.io.File;
import java.io.IOException;
import org.dromara.hodor.actuator.common.core.ExecutableJob;
import org.dromara.hodor.actuator.common.core.JobLoggerManager;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.common.utils.FileIOUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;

/**
 * Job runnable
 *
 * @author tomgs
 * @since 2021/11/23
 */
public interface JobRunnable {

    /**
     * 执行任务
     *
     * @param executableJob 待执行任务
     * @return 任务执行结果
     * @throws JobExecutionException 任务执行异常
     */
    Object execute(ExecutableJob executableJob) throws Exception;

    /**
     * 停止任务
     *
     * @param executableJob 待执行任务
     * @throws JobExecutionException 任务停止异常
     */
    void stop(ExecutableJob executableJob) throws Exception;

    /**
     * 获取任务状态
     *
     * @param executableJob 待执行任务
     * @return JobExecuteStatus 任务状态
     * @throws JobExecutionException 任务状态异常
     */
    default JobExecuteStatus status(ExecutableJob executableJob) throws Exception {
        if (executableJob == null) {
            return JobExecuteStatus.FINISHED;
        }
        return executableJob.getExecuteStatus();
    }

    /**
     * 获取任务日志
     *
     * @param executableJob 待执行任务
     * @param offset 日志起始位置
     * @param length 日志长度
     * @return 日志数据
     * @throws IOException 读取日志文件异常
     */
    default FileIOUtils.LogData getLog(ExecutableJob executableJob, int offset, int length) throws Exception {
        JobKey jobKey = executableJob.getJobKey();
        File jobLoggerFile = JobLoggerManager.getInstance()
            .buildJobLoggerFile(executableJob.getDataPath(), jobKey.getGroupName(), jobKey.getJobName(), executableJob.getRequestId());
        return FileIOUtils.readUtf8File(jobLoggerFile, offset, length);
    }

}
