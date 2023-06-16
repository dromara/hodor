package org.dromara.hodor.server.api;

import cn.hutool.core.lang.TypeReference;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.cron.CronUtils;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.enums.Priority;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.dromara.hodor.remoting.api.http.HodorRestClient;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.scheduler.api.exception.CreateJobException;
import org.dromara.hodor.server.executor.JobExecutorTypeManager;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegistryService;

/**
 * scheduler controller
 *
 * @author tomgs
 * @since 1.0
 */
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

    @RestMethod("jobTypeNames")
    public HodorResult<String> jobTypeNames(List<String> jobTypeNames) {
        // TODO: create job types
        return HodorResult.success("success");
    }

    @RestMethod("createJob")
    public HodorResult<String> createJob(JobInfo jobInfo) {
        // check arguments
        checkJobInfo(jobInfo);
        resetJobInfo(jobInfo);
        if (jobInfoService.isExists(jobInfo)) {
            return HodorResult.success(StringUtils.format("job {} has exists in scheduler.", JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName())));
        }
        jobInfoService.addJob(jobInfo);
        if (CronUtils.isDisabledCron(jobInfo.getCron())) {
            jobInfoService.updateJobStatus(jobInfo, JobStatus.RUNNING);
            return HodorResult.success("createJob success");
        }
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            return HodorResult.failure("not found active copy set by hash id " + jobInfo.getHashId());
        }
        CopySet copySet = copySetOptional.get();
        copySet.getServers()
            .forEach(server -> forwardDoCreateJob(server, jobInfo));
        return HodorResult.success("createJob success");
    }

    @RestMethod("doCreateJob")
    public HodorResult<String> doCreateJob(JobInfo jobInfo) {
        String serverEndpoint = registryService.getServerEndpoint();
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            return HodorResult.failure("not found active copy set by hash id " + jobInfo.getHashId());
        }
        CopySet copySet = copySetOptional.get();
        // 主节点数据
        if (serverEndpoint.equals(copySet.getLeader())) {
            DataInterval dataInterval = copySet.getDataInterval();
            HodorScheduler activeScheduler = schedulerManager.createActiveSchedulerIfAbsent(serverEndpoint, copySet.getId(), dataInterval);
            if (activeScheduler.checkExists(jobInfo)) {
                return HodorResult.success(StringUtils.format("job {} exist in active scheduler {}",
                    JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), activeScheduler.getSchedulerName()));
            }
            jobInfoService.updateJobStatus(jobInfo, JobStatus.RUNNING);
            activeScheduler.addJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));
            return HodorResult.success(StringUtils.format("add job {} to active scheduler {} success",
                JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), activeScheduler.getSchedulerName()));
        }
        // 备用节点数据
        if (copySet.getServers().contains(serverEndpoint)) {
            DataInterval standbyDataInterval = copySet.getDataInterval();
            HodorScheduler standbyScheduler = schedulerManager.createStandbySchedulerIfAbsent(serverEndpoint, copySet.getId(), standbyDataInterval);
            if (standbyScheduler.checkExists(jobInfo)) {
                return HodorResult.success(StringUtils.format("job {} exist in standby scheduler {}",
                    JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), standbyScheduler.getSchedulerName()));
            }
            standbyScheduler.addJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));
            return HodorResult.success(StringUtils.format("add job {} to standby scheduler {} success",
                JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), standbyScheduler.getSchedulerName()));
        }
        return HodorResult.failure(StringUtils.format("{} add job {} failure",
            serverEndpoint, JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName())));
    }

    @RestMethod("batchCreateJob")
    public HodorResult<String> batchCreateJob(List<JobDesc> jobs) {
        for (JobDesc jobInstance : jobs) {
            JobInfo jobInfo = convertJobInfo(jobInstance);
            if (!jobInfoService.isRunningJob(jobInfo)) {
                createJob(jobInfo);
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
        return HodorResult.success("copySet leader switch success.");
    }

    private void resetJobInfo(JobInfo jobInfo) {
        jobInfo.setHashId(HashUtils.hash(jobInfo.getGroupName() + jobInfo.getJobName()));
        jobInfo.setJobStatus(JobStatus.READY);
    }

    private void checkJobInfo(JobInfo jobInfo) {
        String groupName = jobInfo.getGroupName();
        String jobName = jobInfo.getJobName();
        String cron = jobInfo.getCron();
        Priority priority = jobInfo.getPriority();
        Assert.notBlank(groupName, "group name must be not null.");
        Assert.notBlank(jobName, "job name must be not null.");
        Assert.notBlank(cron, "cron must be not null.");
        Assert.notNull(priority, "priority must be not null.");
        CronUtils.assertValidCron(cron, "cron {} is invalid.", cron);
    }

    private void forwardDoCreateJob(String server, JobInfo jobInfo) {
        //转发到对应节点
        HodorRestClient hodorRestClient = HodorRestClient.getInstance();
        HodorHttpRequest request = new HodorHttpRequest();
        request.setUri("/hodor/scheduler/doCreateJob");
        request.setMethod("POST");
        request.setContent(SerializeUtils.serialize(jobInfo));
        CompletableFuture<HodorHttpResponse> future = hodorRestClient.sendHttpRequest(Host.of(server), request);
        HodorHttpResponse hodorHttpResponse;
        try {
            hodorHttpResponse = future.get();
        } catch (Exception e) {
            /*if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }*/
            throw new CreateJobException(StringUtils.format("create job {} exception, msg: {}.",
                JobKey.of(jobInfo.getGroupName(), jobInfo.getJobName()), e.getMessage()), e);
        }
        if (hodorHttpResponse.getStatusCode() != 200) {
            throw new CreateJobException(new String(hodorHttpResponse.getBody(), StandardCharsets.UTF_8));
        }
        TypeReference<HodorResult<String>> typeReference = new TypeReference<HodorResult<String>>() {};
        HodorResult<String> result = SerializeUtils.deserialize(hodorHttpResponse.getBody(), typeReference.getType());
        if (!result.isSuccess()) {
            throw new CreateJobException(result.getData());
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
