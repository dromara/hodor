package org.dromara.hodor.scheduler.quartz;

import lombok.Setter;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.scheduler.api.JobExecutor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 *  hodor job
 *
 * @author tomgs
 * @version 2020/6/24 1.0 
 */
public class HodorJob implements Job {

    @Setter
    private JobExecutor jobExecutor;

    @Setter
    private JobInfo jobInfo;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        jobExecutor.execute(new HodorJobExecutionContext(jobInfo));
    }

}
