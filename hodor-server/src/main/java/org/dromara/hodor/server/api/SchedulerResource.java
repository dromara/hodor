package org.dromara.hodor.server.api;

import cn.hutool.core.lang.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.cron.CronUtils;
import org.dromara.hodor.common.utils.DateUtils;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.actuator.JobTypeInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.enums.Priority;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.model.scheduler.SchedulerInfo;
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
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.manager.MetadataManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegistryService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * scheduler controller
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@HodorRestService(value = "scheduler", desc = "scheduler rest service")
@SuppressWarnings("unused")
public class SchedulerResource {

    private final RegistryService registryService;

    private final JobInfoService jobInfoService;

    private final SchedulerManager schedulerManager;

    public SchedulerResource(final RegistryService registryService, final JobInfoService jobInfoService) {
        this.registryService = registryService;
        this.jobInfoService = jobInfoService;
        this.schedulerManager = SchedulerManager.getInstance();
    }

    @RestMethod("isAlive")
    public HodorResult<String> isAlive() {
        return HodorResult.success("success");
    }

    /**
     * 将结果上传到注册中心
     */
    @RestMethod("jobTypeNames")
    public HodorResult<String> jobTypeNames(JobTypeInfo jobTypeInfo) {
        registryService.createJobTypeNames(jobTypeInfo);
        return HodorResult.success("success");
    }

    /**
     * 读取注册中心的任务类型
     */
    @RestMethod("getJobTypeNames")
    public HodorResult<List<String>> getJobTypeNames(String clusterName) {
        // TODO: 判断空值
        List<String> jobTypeNames = registryService.getJobTypeNames(clusterName);
        return HodorResult.success("success", jobTypeNames);
    }

    @RestMethod("nodeInfos")
    public HodorResult<List<SchedulerInfo>> nodeInfos() {
        List<SchedulerInfo> schedulerInfos = new ArrayList<>();
        final List<String> runningNodes = registryService.getRunningNodes();
        for (String runningNode : runningNodes) {
            Optional.ofNullable(registryService.getServerNodeInfo(runningNode))
                .ifPresent(e -> {
                    final SchedulerInfo schedulerInfo = Jsons.toBean(e, SchedulerInfo.class);
                    schedulerInfos.add(schedulerInfo);
                });
        }
        return HodorResult.success("success", schedulerInfos);
    }

    @RestMethod("metadata")
    public HodorResult<HodorMetadata> metadata(boolean localed) {
        HodorMetadata metadata;
        if (localed) {
            metadata = MetadataManager.getInstance().getMetadata();
        } else {
            metadata = registryService.getMetadata();
        }
        return HodorResult.success("success", metadata);
    }

    @RestMethod("updateMetadata")
    public HodorResult<HodorMetadata> updateMetadata(HodorMetadata metadata) {
        final HodorMetadata localMetadata = MetadataManager.getInstance().getMetadata();
        if (MetadataManager.getInstance().isEqual(localMetadata, metadata)) {
            return HodorResult.failure("metadata unchanged");
        }
        registryService.createMetadata(metadata);
        return HodorResult.success("success", metadata);
    }

    @RestMethod("createJob")
    public HodorResult<String> createJob(JobInfo jobInfo) {
        // check arguments
        resetJobInfo(jobInfo);
        checkJobInfo(jobInfo);
        if (!jobInfoService.isExists(jobInfo)) {
            jobInfoService.addJob(jobInfo);
        }
        if (CronUtils.isDisabledCron(jobInfo.getCron())) {
            jobInfoService.updateJobStatus(jobInfo, JobStatus.RUNNING);
            return HodorResult.success("createJob success");
        }
        createOrUpdate(jobInfo);
        jobInfoService.updateJobStatus(jobInfo, JobStatus.RUNNING);
        return HodorResult.success("createJob success");
    }

    @RestMethod("batchCreateJob")
    public HodorResult<String> batchCreateJob(List<JobDesc> jobs) {
        for (JobDesc jobInstance : jobs) {
            JobInfo jobInfo = convertJobInfo(jobInstance);
            if (!jobInfoService.isRunningJob(jobInfo)) {
                HodorResult<String> result = createJob(jobInfo);
                log.info("Create job result: {}", result);
            }
        }
        // fireBatchJobCreateEvent();
        return HodorResult.success("batch create job success");
    }

    @RestMethod("copySetLeaderSwitch")
    public HodorResult<String> copySetLeaderSwitch(boolean activeOrStandbyShift, CopySet copySet) {
        String serverEndpoint = registryService.getServerEndpoint();
        List<String> servers = copySet.getServers();
        if (!servers.contains(serverEndpoint)) {
            return HodorResult.failure(StringUtils.format("copySet {} servers not contains server {}.", copySet.getId(), serverEndpoint));
        }
        // activeOrStandbyShift true: standby -> active, false: active -> standby
        if (activeOrStandbyShift) {
            HodorScheduler standbyScheduler = schedulerManager.getStandbyScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
            schedulerManager.addActiveScheduler(standbyScheduler);
        } else {
            HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
            schedulerManager.addStandByScheduler(activeScheduler);
        }
        return HodorResult.success("copySet leader switch success");
    }

    @RestMethod("updateJob")
    public HodorResult<String> updateJob(JobInfo jobInfo) {
        checkJobInfo(jobInfo);
        createOrUpdate(jobInfo);
        return HodorResult.success("update job success");
    }

    @RestMethod("deleteJob")
    public HodorResult<String> deleteJob(JobInfo jobInfo) {
        checkJobInfo(jobInfo);
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobInfo.getHashId());
        }
        JobCommand<JobInfo> jobCommand = new JobCommand<>(JobCommandType.JOB_DELETE_CMD, jobInfo);
        CopySet copySet = copySetOptional.get();
        copySet.getServers()
            .forEach(server -> forwardDoJobCommand(server, jobCommand));
        return HodorResult.success("delete job success");
    }

    @RestMethod("stopJob")
    public HodorResult<String> stopJob(JobInfo jobInfo) {
        checkJobInfo(jobInfo);
        return deleteJob(jobInfo);
    }

    @RestMethod("executeJob")
    public HodorResult<String> executeJob(JobInfo jobInfo) {
        checkJobInfo(jobInfo);
        // 1、判断是 common_job 还是 cron = "-"，这种任务直接通过hashId定位到主节点去执行
        // 2、如果是定时任务需要找到任务在某一个节点上再去执行
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobInfo.getHashId());
        }
        JobCommand<JobInfo> jobCommand = new JobCommand<>(JobCommandType.JOB_EXECUTE_CMD, jobInfo);
        CopySet copySet = copySetOptional.get();
        forwardDoJobCommand(copySet.getLeader(), jobCommand);
        return HodorResult.success("execute job success");
    }

    @RestMethod("doJobCommand")
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
                    activeScheduler.putJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("add job {} to active scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), activeScheduler.getSchedulerName()));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    DataInterval standbyDataInterval = copySet.getDataInterval();
                    HodorScheduler standbyScheduler = schedulerManager.createStandbySchedulerIfAbsent(serverEndpoint, copySet.getId(), standbyDataInterval);
                    standbyScheduler.putJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("add job {} to standby scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), standbyScheduler.getSchedulerName()));
                }
            case JOB_UPDATE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    DataInterval dataInterval = copySet.getDataInterval();
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    activeScheduler.putJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("update job {} to active scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), activeScheduler.getSchedulerName()));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    DataInterval standbyDataInterval = copySet.getDataInterval();
                    HodorScheduler standbyScheduler = schedulerManager.getStandbyScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                    standbyScheduler.putJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));
                    return HodorResult.success(StringUtils.format("update job {} to standby scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), standbyScheduler.getSchedulerName()));
                }
            case JOB_DELETE_CMD:
                if (serverEndpoint.equals(copySet.getLeader())) {
                    DataInterval dataInterval = copySet.getDataInterval();
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerName);
                    activeScheduler.deleteJob(jobInfo);
                    return HodorResult.success(StringUtils.format("delete job {} from active scheduler {} success",
                        JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), schedulerName));
                }
                if (copySet.getServers().contains(serverEndpoint)) {
                    DataInterval standbyDataInterval = copySet.getDataInterval();
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
                if (jobInfo.getJobType() == JobType.COMMON_JOB) {
                    HodorJobExecutionContext executionContext = new HodorJobExecutionContext(null, jobInfo, schedulerName, DateUtils.nowDate());
                    JobExecutor jobExecutor = JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType());
                    jobExecutor.execute(executionContext);
                } else {
                    DataInterval dataInterval = copySet.getDataInterval();
                    HodorScheduler activeScheduler = schedulerManager.getActiveScheduler(schedulerName);
                    activeScheduler.triggerJob(jobInfo);
                }
                return HodorResult.success(StringUtils.format("execute job {} from active scheduler {} success",
                    JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), schedulerName));
        }
        return HodorResult.failure(StringUtils.format("DoJobCommand {} failure, ServerEndpoint {}, JobKey {}",
            commandType.name(), serverEndpoint, JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName())));
    }

    private void createOrUpdate(JobInfo jobInfo) {
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            throw new CreateJobException("not found active copy set by hash id " + jobInfo.getHashId());
        }
        JobCommand<JobInfo> jobCommand = new JobCommand<>(JobCommandType.JOB_CREATE_CMD, jobInfo);
        CopySet copySet = copySetOptional.get();
        copySet.getServers()
            .forEach(server -> forwardDoJobCommand(server, jobCommand));
    }

    private void resetJobInfo(JobInfo jobInfo) {
        jobInfo.setHashId(HashUtils.hash(jobInfo.getGroupName() + jobInfo.getJobName()));
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setFireNow(jobInfo.getFireNow() != null && jobInfo.getFireNow());
        jobInfo.setPriority(jobInfo.getPriority() == null ? Priority.DEFAULT : jobInfo.getPriority());
        jobInfo.setFailover(jobInfo.getFailover() != null && jobInfo.getFailover());
        jobInfo.setCreateTime(DateUtils.nowDate());
    }

    private void checkJobInfo(JobInfo jobInfo) {
        String groupName = jobInfo.getGroupName();
        String jobName = jobInfo.getJobName();
        String cron = jobInfo.getCron();
        Priority priority = jobInfo.getPriority();
        Long hashId = jobInfo.getHashId();
        Assert.notBlank(groupName, "group name must be not null");
        Assert.notBlank(jobName, "job name must be not null");
        Assert.notBlank(cron, "cron must be not null");
        Assert.notNull(priority, "priority must be not null");
        Assert.notNull(hashId, "hashId must be not null");
        CronUtils.assertValidCron(cron, "cron {} is invalid", cron);
    }

    private void forwardDoJobCommand(String server, JobCommand<JobInfo> jobCommand) {
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
            throw new OperateJobException(result.getData());
        }
    }

    private JobInfo convertJobInfo(JobDesc job) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setHashId(HashUtils.hash(job.getGroupName() + job.getJobName()));
        jobInfo.setGroupName(job.getGroupName());
        jobInfo.setJobName(job.getJobName());
        jobInfo.setJobCommandType(job.getJobCommandType());
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setCron(job.getCron());
        jobInfo.setTimeout(job.getTimeout());
        jobInfo.setFireNow(job.getFireNow());
        jobInfo.setIsBroadcast(job.getIsBroadcast());
        jobInfo.setJobType(JobType.TIME_JOB);
        jobInfo.setPriority(Priority.DEFAULT);
        return jobInfo;
    }

}
