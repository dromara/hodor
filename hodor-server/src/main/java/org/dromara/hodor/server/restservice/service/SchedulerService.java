package org.dromara.hodor.server.restservice.service;

import java.util.List;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.server.restservice.HodorRestService;

/**
 * scheduler controller
 *
 * @author tomgs
 * @since 2021/1/28
 */
@HodorRestService(value = "scheduler", desc = "scheduler rest service")
@SuppressWarnings("unused")
public class SchedulerService {

    public HodorResult<String> isAlive() {
        return HodorResult.success("success");
    }

    public HodorResult<String> createJob(List<JobDesc> jobs) {
        for (JobDesc job : jobs) {
            System.out.println(job);
        }
        return HodorResult.success("success");
    }

}
