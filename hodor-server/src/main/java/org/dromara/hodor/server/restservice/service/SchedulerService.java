package org.dromara.hodor.server.restservice.service;

import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.job.JobInstance;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.dromara.hodor.remoting.api.http.HodorRestClient;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.scheduler.api.exception.CreateJobException;
import org.dromara.hodor.server.component.EventType;
import org.dromara.hodor.server.executor.JobExecutorTypeManager;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegisterService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * scheduler controller
 *
 * @author tomgs
 * @since 2021/1/28
 */
@HodorRestService(value = "scheduler", desc = "scheduler rest service")
@SuppressWarnings("unused")
public class SchedulerService {

    private final RegisterService registerService;

    private final JobInfoService jobInfoService;

    private final SchedulerManager schedulerManager;

    public SchedulerService(final RegisterService registerService, final JobInfoService jobInfoService) {
        this.registerService = registerService;
        this.jobInfoService = jobInfoService;
        this.schedulerManager = SchedulerManager.getInstance();
    }

    @RestMethod("isAlive")
    public HodorResult<String> isAlive() {
        return HodorResult.success("success");
    }

    @RestMethod("createJob")
    public HodorResult<String> createJob(JobInstance jobInstance) {
        JobInfo jobInfo = convertJobInfo(jobInstance);
        jobInfoService.addJobIfAbsent(jobInfo);
        String serverEndpoint = registerService.getServerEndpoint();
        Optional<CopySet> copySetOptional = CopySetManager.getInstance().getCopySetByInterval(jobInfo.getHashId());
        if (!copySetOptional.isPresent()) {
            return HodorResult.failure("not found active copy set by hash id " + jobInfo.getHashId());
        }
        CopySet copySet = copySetOptional.get();
        // 主节点数据
        if (serverEndpoint.equals(copySet.getLeader())) {
            DataInterval dataInterval = copySet.getDataInterval();
            HodorScheduler activeScheduler = schedulerManager.createActiveScheduler(copySet.getServerId(), dataInterval);
            activeScheduler.addJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));

            jobInfoService.updateJobStatus(jobInfo, JobStatus.RUNNING);
            return HodorResult.success(StringUtils.format("add to active scheduler {} success", activeScheduler.getSchedulerName()));
        }
        // 备用节点数据
        if (copySet.getServers().contains(serverEndpoint)) {
            DataInterval standbyDataInterval = copySet.getDataInterval();
            HodorScheduler standbyScheduler = schedulerManager.createStandbyScheduler(copySet.getServerId(), standbyDataInterval);
            standbyScheduler.addJob(jobInfo, JobExecutorTypeManager.getInstance().getJobExecutor(jobInfo.getJobType()));
            return HodorResult.success(StringUtils.format("add to standby scheduler {} success", standbyScheduler.getSchedulerName()));
        }

        copySet.getServers().stream()
                .filter(server -> !serverEndpoint.equals(server))
                .forEach(server -> forwardToServer(server, jobInfo));
        return HodorResult.success("success");
    }

    private void forwardToServer(String server, JobInfo jobInfo) {
        //转发到对应节点
        HodorRestClient hodorRestClient = HodorRestClient.getInstance();
        HodorHttpRequest request = new HodorHttpRequest();
        request.setUri("/hodor/scheduler/createJob");
        request.setMethod("POST");
        request.setContent(SerializeUtils.serialize(jobInfo));
        CompletableFuture<HodorHttpResponse> future = hodorRestClient.sendHttpRequest(Host.of(server), request);
        try {
            HodorHttpResponse hodorHttpResponse = future.get();
        } catch (Exception e) {
            throw new CreateJobException(StringUtils.format("create job {}@{} exception, msg: {}.",
                    jobInfo.getGroupName(), jobInfo.getJobName(), e.getMessage()), e);
        }
    }

    @RestMethod("batchCreateJob")
    public HodorResult<String> batchCreateJob(List<JobInstance> jobs) {
        for (JobInstance jobInstance : jobs) {
            JobInfo jobInfo = convertJobInfo(jobInstance);
            jobInfoService.addJobIfAbsent(jobInfo);
        }
        fireJobCreateEvent();
        return HodorResult.success("success");
    }

    @RestMethod("onEvent")
    public HodorResult<String> onEvent(Event<Object> event) {
        String serverEndpoint = registerService.getServerEndpoint();
        List<CopySet> copySets = CopySetManager.getInstance().getCopySet(serverEndpoint);

        return HodorResult.success("success");
    }

    private void fireJobCreateEvent() {
        HodorRestClient hodorRestClient = HodorRestClient.getInstance();
        //client.sendHttpRequest();
        List<String> nodes = registerService.getRunningNodes();
        for (String endpoint : nodes) {
            HodorHttpRequest request = new HodorHttpRequest();
            request.setUri("/hodor/scheduler/onEvent");
            request.setMethod("POST");
            request.setContent(SerializeUtils.serialize(Event.create("create_job", EventType.JOB_CREATE_DISTRIBUTE)));
            CompletableFuture<HodorHttpResponse> future = hodorRestClient.sendHttpRequest(Host.of(endpoint), request);
        }
    }

    private JobInfo convertJobInfo(JobInstance job) {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setHashId(HashUtils.hash(job.getGroupName() + job.getJobName()));
        jobInfo.setGroupName(job.getGroupName());
        jobInfo.setJobName(job.getJobName());
        jobInfo.setJobCommandType(job.getCommandType());
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setCron(job.getCron());
        jobInfo.setTimeout(job.getTimeout());
        jobInfo.setFireNow(job.isFireNow());
        jobInfo.setIsBroadcast(job.isBroadcast());
        jobInfo.setJobType(JobType.TIME_JOB);
        return jobInfo;
    }

}
