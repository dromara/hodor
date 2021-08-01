package org.dromara.hodor.server.restservice.service;

import java.util.List;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.job.JobInstance;
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

    public SchedulerService(final LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    public HodorResult<String> isAlive() {
        return HodorResult.success("success");
    }

    public HodorResult<String> createJob(List<JobInstance> jobs) {
        if (!leaderService.isLeader()) {
            // redirect request to leader
            String leaderServerId = leaderService.getLeaderEndpoint();
            
        }
        for (JobInstance job : jobs) {
            System.out.println(job);
        }
        return HodorResult.success("success");
    }

}
