package org.dromara.hodor.server.api;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.common.utils.Utils.Dates;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.actuator.JobTypeInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.*;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.model.scheduler.SchedulerInfo;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.server.common.JobCommand;
import org.dromara.hodor.server.executor.JobExecutorTypeManager;
import org.dromara.hodor.server.manager.JobOperatorManager;
import org.dromara.hodor.server.manager.MetadataManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegistryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private final JobOperatorManager jobOperatorManager;

    public SchedulerResource(final RegistryService registryService, final JobInfoService jobInfoService) {
        this.registryService = registryService;
        this.jobInfoService = jobInfoService;
        this.schedulerManager = SchedulerManager.getInstance();
        this.jobOperatorManager = new JobOperatorManager(JobExecutorTypeManager.getInstance(),
            schedulerManager,
            registryService);
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
        Assert.notNull(clusterName, "clusterName can not be null");
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
        if (TimeType.NONE == jobInfo.getTimeType()) {
            jobInfoService.updateJobStatus(jobInfo, JobStatus.RUNNING);
            return HodorResult.success("createJob success");
        }
        jobOperatorManager.createOrUpdate(jobInfo);
        jobInfoService.updateJobStatus(jobInfo, JobStatus.RUNNING);
        return HodorResult.success("createJob success");
    }

    @RestMethod("batchCreateJob")
    public HodorResult<String> batchCreateJob(List<JobDesc> jobs) {
        for (JobDesc jobInstance : jobs) {
            JobInfo jobInfo = convertJobInfo(jobInstance);
            if (jobInfoService.runnableJob(jobInfo)) {
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
        jobOperatorManager.createOrUpdate(jobInfo);
        return HodorResult.success("update job success");
    }

    @RestMethod("deleteJob")
    public HodorResult<String> deleteJob(JobInfo jobInfo) {
        checkJobInfo(jobInfo);
        jobOperatorManager.deleteJob(jobInfo);
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
        jobOperatorManager.executeJob(jobInfo);
        return HodorResult.success("execute job success");
    }

    @RestMethod("doJobCommand")
    public HodorResult<String> doJobCommand(JobCommand<JobInfo> jobCommand) {
        return jobOperatorManager.doJobCommand(jobCommand);
    }

    private void resetJobInfo(JobInfo jobInfo) {
        jobInfo.setHashId(HashUtils.hash(jobInfo.getGroupName() + jobInfo.getJobName()));
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setPriority(jobInfo.getPriority() == null ? Priority.DEFAULT : jobInfo.getPriority());
        jobInfo.setScheduleStrategy(jobInfo.getScheduleStrategy() == null ? ScheduleStrategy.RANDOM : jobInfo.getScheduleStrategy());
        jobInfo.setFireNow(jobInfo.getFireNow() != null && jobInfo.getFireNow());
        jobInfo.setFailover(jobInfo.getFailover() != null && jobInfo.getFailover());
        jobInfo.setMisfire(jobInfo.getMisfire() != null && jobInfo.getMisfire());
        jobInfo.setCreateTime(Dates.date());
    }

    private void checkJobInfo(JobInfo jobInfo) {
        String groupName = jobInfo.getGroupName();
        String jobName = jobInfo.getJobName();
        String timeExp = jobInfo.getTimeExp();
        Priority priority = jobInfo.getPriority();
        Long hashId = jobInfo.getHashId();

        Assert.notBlank(groupName, "group name must be not null");
        Assert.notBlank(jobName, "job name must be not null");
        if (jobInfo.getTimeType() != TimeType.NONE) {
            Assert.notBlank(timeExp, "cron must be not null");
        }
        Assert.notNull(priority, "priority must be not null");
        Assert.notNull(hashId, "hashId must be not null");
    }

    private JobInfo convertJobInfo(JobDesc job) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setHashId(HashUtils.hash(job.getGroupName() + job.getJobName()));
        jobInfo.setGroupName(job.getGroupName());
        jobInfo.setJobName(job.getJobName());
        jobInfo.setJobCommandType(job.getJobCommandType());
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setTimeType(job.getTimeType());
        jobInfo.setTimeExp(job.getTimeExp());
        jobInfo.setTimeout(job.getTimeout());
        jobInfo.setFireNow(job.getFireNow());
        jobInfo.setJobType(JobType.COMMON_JOB);
        jobInfo.setPriority(Priority.DEFAULT);
        return jobInfo;
    }

}
