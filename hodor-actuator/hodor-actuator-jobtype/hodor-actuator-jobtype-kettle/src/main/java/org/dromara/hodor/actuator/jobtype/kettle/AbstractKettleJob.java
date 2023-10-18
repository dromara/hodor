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

import java.nio.file.Paths;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.jobtype.api.executor.AbstractJob;
import org.dromara.hodor.actuator.jobtype.api.executor.AbstractProcessJob;
import org.dromara.hodor.actuator.jobtype.api.executor.CommonJobProperties;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.common.utils.StringUtils;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEventListener;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.repository.Repository;

/**
 * AbstractKettleJob
 *
 * @author tomgs
 * @since 1.0
 */
public abstract class AbstractKettleJob<T> extends AbstractJob {

    private final String jobId;

    private final Props sysProps;

    private final Props jobProps;

    private final Logger log;

    public AbstractKettleJob(String jobId, Props sysProps, Props jobProps, Logger log) {
        super(jobId, sysProps, jobProps, log);
        this.jobId = jobId;
        this.sysProps = sysProps;
        this.jobProps = jobProps;
        this.log = log;
    }

    @Override
    public void run() throws Exception {
        ExecutableJobContext jobContext = this.jobProps.getObj(CommonJobProperties.JOB_CONTEXT);
        log.info("Start running kettle job [{}]", jobId);

        String jobName = jobProps.getString(KettleConstant.NAME, jobContext.getJobKey().getJobName());
        String repositoryType = jobProps.getString(KettleConstant.REPOSITORY_TYPE, KettleConstant.FILE_TYPE);
        String repositoryPath = jobProps.getString(KettleConstant.REPOSITORY_PATH,
            sysProps.getString("kettle.repository", ""));
        String path = Paths.get(jobProps.getString(AbstractProcessJob.WORKING_DIR), jobName).toString();
        String plugins = jobProps.getString(KettleConstant.PLUGINS,
            sysProps.getString("kettle.plugins", ""));
        String loglevel = jobProps.getString(KettleConstant.LOG_LEVEL, "Debug");

        log.info("Kettle config repository type [{}]", repositoryType);
        log.info("Kettle config repository path [{}]", repositoryPath);
        log.info("Kettle config path [{}]", path);
        log.info("Kettle config plugins [{}]", plugins);
        log.info("Kettle config loglevel [{}]", loglevel);
        log.info("Kettle config jobName [{}]", jobName);

        // 1、初始化环境
        KettleProcess.init(plugins);

        // 2、设置资源库
        Repository repository = KettleProcess.getRepository(repositoryType, repositoryPath, jobName, sysProps);

        // 日志级别，默认使用配置文件的
        LogLevel logLevel = LogLevel.BASIC;
        if (StringUtils.isNotEmpty(loglevel)) {
            logLevel = KettleProcess.bulidLogLevel(loglevel);
        }
        log.info("Kettle using log level {}", logLevel);

        final T executor = buildKettleExecutor(repository, path, jobName, logLevel);

        // 添加运行日志监听
        KettleLoggingEventListener kettleLogListener = logs -> {
            if (logs.getLevel() == LogLevel.ERROR) {
                log.error(logs.getMessage());
            } else {
                log.info(logs.getMessage());
            }
        };
        KettleLogStore.getAppender().addLoggingEventListener(kettleLogListener);

        int errors = execute(executor, jobContext.getExecuteRequest().getTimeout());

        // 删除运行日志监听
        KettleLogStore.getAppender().removeLoggingEventListener(kettleLogListener);

        if (errors != 0) {
            log.error("Kettle run failed");
            throw new JobExecutionException("Kettle run failed, jobKey:" + jobId);
        }

        log.info("Kettle run finished");

    }

    public abstract T buildKettleExecutor(Repository repository, String path, String jobName, LogLevel logLevel) throws Exception;

    public abstract int execute(T t, int timeout) throws Exception;

}
