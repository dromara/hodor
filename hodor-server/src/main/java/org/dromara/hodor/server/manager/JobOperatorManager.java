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
package org.dromara.hodor.server.manager;

import cn.hutool.core.lang.TypeReference;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.dromara.hodor.remoting.api.http.HodorRestClient;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.JobExecutor;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.scheduler.api.exception.CreateJobException;
import org.dromara.hodor.server.common.JobCommand;
import org.dromara.hodor.server.common.JobCommandType;
import org.dromara.hodor.server.executor.JobExecutorTypeManager;
import org.dromara.hodor.server.executor.exception.OperateJobException;
import org.dromara.hodor.server.service.RegistryService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * JobOperatorManager
 *
 * @author tomgs
 * @since 1.0
 */
public class JobOperatorManager {

    private final JobExecutorTypeManager executorTypeManager;

    private final SchedulerManager schedulerManager;
    private final RegistryService registryService;

    public JobOperatorManager(final JobExecutorTypeManager executorTypeManager,
                              final SchedulerManager schedulerManager,
                              final RegistryService registryService) {
        this.executorTypeManager = executorTypeManager;
        this.schedulerManager = schedulerManager;
        this.registryService = registryService;
    }

    public void createOrUpdate(JobInfo jobInfo) {
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobInfo.getHashId());
        }
        JobCommand<JobInfo> jobCommand = new JobCommand<>(JobCommandType.JOB_CREATE_CMD, jobInfo);
        CopySet copySet = copySetOptional.get();
        copySet.getServers()
            .forEach(server -> forwardDoJobCommand(server, jobCommand));
    }

    public void deleteJob(JobInfo jobInfo) {
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobInfo.getHashId());
        }
        JobCommand<JobInfo> jobCommand = new JobCommand<>(JobCommandType.JOB_DELETE_CMD, jobInfo);
        CopySet copySet = copySetOptional.get();
        copySet.getServers()
            .forEach(server -> forwardDoJobCommand(server, jobCommand));
    }

    public void executeJob(JobInfo jobInfo) {
        // 1、判断是 common_job 还是 cron = "-"，这种任务直接通过hashId定位到主节点去执行
        // 2、如果是定时任务需要找到任务在某一个节点上再去执行
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobInfo.getHashId());
        }
        JobCommand<JobInfo> jobCommand = new JobCommand<>(JobCommandType.JOB_EXECUTE_CMD, jobInfo);
        CopySet copySet = copySetOptional.get();
        forwardDoJobCommand(copySet.getLeader(), jobCommand);
    }

    public HodorResult<String> doJobCommand(JobCommand<JobInfo> jobCommand) {
        JobCommandType commandType = jobCommand.getCommandType();
        JobInfo jobInfo = jobCommand.getData();

        String serverEndpoint = registryService.getServerEndpoint();
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            return HodorResult.failure(StringUtils.format("DoJobCommand {} failure, not found active copy set by hash id {}", commandType, jobInfo.getHashId()));
        }
        CopySet copySet = copySetOptional.get();
        String schedulerName = schedulerManager.createSchedulerName(serverEndpoint, copySet.getId());
        switch (commandType) {
            case JOB_CREATE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    DataInterval dataInterval = copySet.getDataInterval();
                    HodorScheduler activeScheduler = schedulerManager.createActiveSchedulerIfAbsent(serverEndpoint, copySet.getId(), dataInterval);
                    activeScheduler.putJob(jobInfo, executorTypeManager.getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("add job {} to active scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), activeScheduler.getSchedulerName()));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    DataInterval standbyDataInterval = copySet.getDataInterval();
                    HodorScheduler standbyScheduler = schedulerManager.createStandbySchedulerIfAbsent(serverEndpoint, copySet.getId(), standbyDataInterval);
                    standbyScheduler.putJob(jobInfo, executorTypeManager.getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("add job {} to standby scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), standbyScheduler.getSchedulerName()));
                }
            case JOB_UPDATE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    activeScheduler.putJob(jobInfo, executorTypeManager.getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("update job {} to active scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), activeScheduler.getSchedulerName()));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    HodorScheduler standbyScheduler = schedulerManager.getStandbyScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    standbyScheduler.putJob(jobInfo, executorTypeManager.getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("update job {} to standby scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), standbyScheduler.getSchedulerName()));
                }
            case JOB_DELETE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerName);
                    activeScheduler.deleteJob(jobInfo);
                    return HodorResult.success(StringUtils.format("delete job {} from active scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), schedulerName));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    HodorScheduler standbyScheduler = schedulerManager.getStandbyScheduler(schedulerName);
                    standbyScheduler.deleteJob(jobInfo);
                    return HodorResult.success(StringUtils.format("delete job {} from standby scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), schedulerName));
                }
            case JOB_EXECUTE_CMD:
                if (!serverEndpoint.equals(copySet.getLeader())) {
                    return HodorResult.failure(StringUtils.format("execute job {} failure, current scheduler server {} is not leader",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), serverEndpoint));
                }
                if (jobInfo.getTimeType() == TimeType.NONE || jobInfo.getJobStatus() == JobStatus.STOP) {
                    HodorJobExecutionContext executionContext = new HodorJobExecutionContext(null, jobInfo, schedulerName, Utils.Dates.date());
                    JobExecutor jobExecutor = executorTypeManager.getJobExecutor(jobInfo.getJobType());
                    jobExecutor.execute(executionContext);
                } else {
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerName);
                    if (!activeScheduler.checkExists(jobInfo)) {
                        return HodorResult.failure(StringUtils.format("execute job {} failure, not found job",
                            JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName())));
                    }
                    activeScheduler.triggerJob(jobInfo);
                }
                return HodorResult.success(StringUtils.format("execute job {} from active scheduler {} success",
                    JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), schedulerName));
        }
        return HodorResult.failure(StringUtils.format("DoJobCommand {} failure, ServerEndpoint {}, JobKey {}",
            commandType.name(), serverEndpoint, JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName())));
    }

    public void forwardDoJobCommand(String server, JobCommand<JobInfo> jobCommand) {
        //转发到对应节点
        HodorRestClient hodorRestClient = HodorRestClient.getInstance();
        HodorHttpRequest request = new HodorHttpRequest();

        request.setUri("/hodor/scheduler/doJobCommand");
        request.setMethod("POST");
        request.setContent(SerializeUtils.serialize(jobCommand));
        CompletableFuture<HodorHttpResponse> future = hodorRestClient.sendHttpRequest(Host.of(server), request);
        HodorHttpResponse hodorHttpResponse;
        try {
            hodorHttpResponse = future.get();
        } catch (Exception e) {
            throw new OperateJobException(StringUtils.format("DoJobCommand {} exception, jobKey {}, msg: {}.",
                jobCommand.getCommandType(),
                JobKey.of(jobCommand.getData().getGroupName(), jobCommand.getData().getJobName()), e.getMessage()), e);
        }
        if (hodorHttpResponse.getStatusCode() != 200) {
            throw new OperateJobException(new String(hodorHttpResponse.getBody(), StandardCharsets.UTF_8));
        }
        TypeReference<HodorResult<String>> typeReference = new TypeReference<HodorResult<String>>() {
        };
        HodorResult<String> result = SerializeUtils.deserialize(hodorHttpResponse.getBody(), typeReference.getType());
        if (!result.isSuccess()) {
            throw new OperateJobException(result.getMsg());
        }
    }

}
