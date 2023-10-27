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
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobDesc;
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

    public void createOrUpdate(JobDesc jobDesc) {
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobDesc.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobDesc.getHashId());
        }
        JobCommand<JobDesc> jobCommand = new JobCommand<>(JobCommandType.JOB_CREATE_CMD, jobDesc);
        CopySet copySet = copySetOptional.get();
        copySet.getServers()
            .forEach(server -> forwardDoJobCommand(server, jobCommand));
    }

    public void deleteJob(JobDesc jobDesc) {
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobDesc.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobDesc.getHashId());
        }
        JobCommand<JobDesc> jobCommand = new JobCommand<>(JobCommandType.JOB_DELETE_CMD, jobDesc);
        CopySet copySet = copySetOptional.get();
        copySet.getServers()
            .forEach(server -> forwardDoJobCommand(server, jobCommand));
    }

    public void executeJob(JobDesc jobDesc) {
        // 1、判断是 common_job 还是 cron = "-"，这种任务直接通过hashId定位到主节点去执行
        // 2、如果是定时任务需要找到任务在某一个节点上再去执行
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobDesc.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobDesc.getHashId());
        }
        JobCommand<JobDesc> jobCommand = new JobCommand<>(JobCommandType.JOB_EXECUTE_CMD, jobDesc);
        CopySet copySet = copySetOptional.get();
        forwardDoJobCommand(copySet.getLeader(), jobCommand);
    }

    public void reschedule(JobDesc jobDesc) {
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobDesc.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobDesc.getHashId());
        }
        JobCommand<JobDesc> jobCommand = new JobCommand<>(JobCommandType.JOB_RESCHEDULE_CMD, jobDesc);
        CopySet copySet = copySetOptional.get();
        forwardDoJobCommand(copySet.getLeader(), jobCommand);
    }

    public HodorResult<String> doJobCommand(JobCommand<JobDesc> jobCommand) {
        JobCommandType commandType = jobCommand.getCommandType();
        JobDesc jobDesc = jobCommand.getData();

        String serverEndpoint = registryService.getServerEndpoint();
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobDesc.getHashId());
        if (!copySetOptional.isPresent()) {
            return HodorResult.failure(StringUtils.format("DoJobCommand {} failure, not found active copy set by hash id {}", commandType, jobDesc.getHashId()));
        }
        CopySet copySet = copySetOptional.get();
        String schedulerName = schedulerManager.createSchedulerName(serverEndpoint, copySet.getId());
        switch (commandType) {
            case JOB_CREATE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    DataInterval dataInterval = copySet.getDataInterval();
                    HodorScheduler activeScheduler = schedulerManager.createActiveSchedulerIfAbsent(serverEndpoint, copySet.getId(), dataInterval);
                    activeScheduler.putJob(jobDesc, executorTypeManager.getJobExecutor(jobDesc.getJobType()));
                    return HodorResult.success(StringUtils.format("add job {} to active scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), activeScheduler.getSchedulerName()));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    DataInterval standbyDataInterval = copySet.getDataInterval();
                    HodorScheduler standbyScheduler = schedulerManager.createStandbySchedulerIfAbsent(serverEndpoint, copySet.getId(), standbyDataInterval);
                    standbyScheduler.putJob(jobDesc, executorTypeManager.getJobExecutor(jobDesc.getJobType()));
                    return HodorResult.success(StringUtils.format("add job {} to standby scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), standbyScheduler.getSchedulerName()));
                }
            case JOB_UPDATE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    activeScheduler.putJob(jobDesc, executorTypeManager.getJobExecutor(jobDesc.getJobType()));
                    return HodorResult.success(StringUtils.format("update job {} to active scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), activeScheduler.getSchedulerName()));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    HodorScheduler standbyScheduler = schedulerManager.getStandbyScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    standbyScheduler.putJob(jobDesc, executorTypeManager.getJobExecutor(jobDesc.getJobType()));
                    return HodorResult.success(StringUtils.format("update job {} to standby scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), standbyScheduler.getSchedulerName()));
                }
            case JOB_DELETE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerName);
                    activeScheduler.deleteJob(jobDesc);
                    return HodorResult.success(StringUtils.format("delete job {} from active scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), schedulerName));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    HodorScheduler standbyScheduler = schedulerManager.getStandbyScheduler(schedulerName);
                    standbyScheduler.deleteJob(jobDesc);
                    return HodorResult.success(StringUtils.format("delete job {} from standby scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), schedulerName));
                }
            case JOB_EXECUTE_CMD:
                if (!serverEndpoint.equals(copySet.getLeader())) {
                    return HodorResult.failure(StringUtils.format("execute job {} failure, current scheduler server {} is not leader",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), serverEndpoint));
                }
                if (jobDesc.getTimeType() == TimeType.NONE || jobDesc.getJobStatus() == JobStatus.STOP) {
                    HodorJobExecutionContext executionContext = new HodorJobExecutionContext(null, jobDesc, schedulerName, Utils.Dates.date());
                    JobExecutor jobExecutor = executorTypeManager.getJobExecutor(jobDesc.getJobType());
                    jobExecutor.execute(executionContext);
                } else {
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerName);
                    if (!activeScheduler.checkExists(jobDesc)) {
                        return HodorResult.failure(StringUtils.format("execute job {} failure, not found job",
                            JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName())));
                    }
                    activeScheduler.triggerJob(jobDesc);
                }
                return HodorResult.success(StringUtils.format("execute job {} from active scheduler {} success",
                    JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), schedulerName));
            case JOB_RESCHEDULE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    activeScheduler.rescheduleJob(jobDesc);
                    return HodorResult.success(StringUtils.format("reschedule job {} to active scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), activeScheduler.getSchedulerName()));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    HodorScheduler standbyScheduler = schedulerManager.getStandbyScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    standbyScheduler.rescheduleJob(jobDesc);
                    return HodorResult.success(StringUtils.format("reschedule job {} to standby scheduler {} success",
                        JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName()), standbyScheduler.getSchedulerName()));
                }
        }
        return HodorResult.failure(StringUtils.format("DoJobCommand {} failure, ServerEndpoint {}, JobKey {}",
            commandType.name(), serverEndpoint, JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName())));
    }

    public void forwardDoJobCommand(String server, JobCommand<JobDesc> jobCommand) {
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
