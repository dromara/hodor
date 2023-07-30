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

package org.dromara.hodor.actuator.jobtype.kettle;

import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.api.utils.Props;
import org.dromara.hodor.actuator.jobtype.api.executor.AbstractJob;
import org.dromara.hodor.actuator.jobtype.api.executor.CommonJobProperties;
import org.dromara.hodor.common.utils.StringUtils;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEventListener;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;

/**
 * KettleJob
 *
 * @author tomgs
 * @since 1.0
 */
public class KettleJob extends AbstractJob {

    private final String jobId;

    private final Props jobProps;

    private final Logger log;

    private Job kettleJob;

    public KettleJob(String jobId, Props sysProps, Props jobProps, Logger log) {
        super(jobId, sysProps, jobProps, log);
        this.jobId = jobId;
        this.jobProps = jobProps;
        this.log = log;
    }

    @Override
    public void run() throws Exception {
        ExecutableJobContext jobContext = this.jobProps.getObj(CommonJobProperties.JOB_CONTEXT);
        log.info("Start running kettle job [{}]", jobId);

        String repositoryPath = jobProps.getString(KettleConstant.REPOSITORY_PATH);
        String repositoryType = jobProps.getString(KettleConstant.REPOSITORY_TYPE);
        String path = jobProps.getString(KettleConstant.PATH);
        String plugins = jobProps.getString(KettleConstant.PLUGINS);
        String loglevel = jobProps.getString(KettleConstant.LOG_LEVEL);

        // 1、初始化环境
        KettleProcess.init(plugins);

        // 2、设置资源库
        KettleFileRepositoryMeta repositoryMeta;
        if (StringUtils.isEmpty(repositoryPath)) {
            log.info("KettleJob repository path is null will using path {}", path);
            repositoryMeta = new KettleFileRepositoryMeta(null, null, null, path);
        } else {
            log.info("KettleJob repository path is {}", repositoryPath);
            repositoryMeta = new KettleFileRepositoryMeta(null, null, null, repositoryPath);
        }
        // 文件资源库
        KettleFileRepository repository = KettleProcess.getFileRepository(repositoryMeta);

        // 日志级别，默认使用配置文件的
        LogLevel logLevel = LogLevel.BASIC;
        if (StringUtils.isNotEmpty(loglevel)) {
            logLevel = KettleProcess.bulidLogLevel(loglevel);
        }
        log.info("KettleJob log level {}", logLevel);

        // 3、执行job
        kettleJob = KettleProcess.createKettleJob(repository, path, jobContext.getJobKey().getJobName(),
            jobProps.getMapByPrefix("kettle.params"),
            jobProps.getMapByPrefix("kettle.variables"));
        // 设置日志级别
        kettleJob.setLogLevel(logLevel);

        // 添加运行日志监听
        KettleLoggingEventListener kettleLogListener = logs -> log.info("KettleJob Running Logs：{}", logs.getMessage());
        KettleLogStore.getAppender().addLoggingEventListener(kettleLogListener);

        kettleJob.start();
        kettleJob.waitUntilFinished(jobContext.getExecuteRequest().getTimeout());
        // 删除运行日志监听
        KettleLogStore.getAppender().removeLoggingEventListener(kettleLogListener);

        if (kettleJob.getErrors() != 0) {
            log.error("KettleJob run failed");
            throw new JobExecutionException("KettleJob run failed, jobKey:" + jobId);
        }

        // 异步的方式
        /*KettleStateTask task = new KettleStateTask();
        task.setJob(kettleJob)
            .setLog(log)
            .setJobContext(jobContext)
            .setLogChannelId(logChannelId)
            .setStartLineIndex(startLineIndex)
            .setRequestId(jobContext.getRequestId())
            .setProps(jobProps);
        AsyncTaskStateChecker stateCheckHandler = AsyncTaskStateChecker.getInstance();
        int queueSize = stateCheckHandler.addTask(task);
        log.info("submit kettle job to task state queue, current queue size : " + queueSize);*/
    }

    @Override
    public void cancel() {
        if (kettleJob != null) {
            kettleJob.stopAll();
        }
    }
}
