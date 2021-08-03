package org.dromara.hodor.server.restservice.service;

import java.util.List;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.job.JobInstance;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.service.LeaderService;

/**
 * scheduler controller
 *
 * @author tomgs
 * @since 2021/1/28
 */
@HodorRestService(value = "scheduler", desc = "scheduler rest service")
@SuppressWarnings("unused")
public class SchedulerService {

    private final LeaderService leaderService;

    private final JobInfoService jobInfoService;

    private final SchedulerManager schedulerManager;

    public SchedulerService(final LeaderService leaderService, final JobInfoService jobInfoService) {
        this.leaderService = leaderService;
        this.jobInfoService = jobInfoService;
        this.schedulerManager = SchedulerManager.getInstance();
    }

    public HodorResult<String> isAlive() {
        return HodorResult.success("success");
    }

    public HodorResult<String> createJob(List<JobInstance> jobs) {
        if (!leaderService.isLeader()) {
            // redirect request to leader
            String leaderServerId = leaderService.getLeaderEndpoint();
            
        }
        for (JobInstance jobInstance : jobs) {
            JobInfo jobInfo = convertJobInfo(jobInstance);
            jobInfoService.addJobIfAbsent(jobInfo);

            //schedulerManager.get

        }
        return HodorResult.success("success");
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
