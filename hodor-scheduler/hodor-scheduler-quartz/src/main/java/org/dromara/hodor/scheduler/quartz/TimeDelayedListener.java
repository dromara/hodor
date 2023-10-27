package org.dromara.hodor.scheduler.quartz;

import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.listeners.JobListenerSupport;

/**
 * TimeDelayedListener
 *
 * @author tomgs
 * @since 1.0
 */
public class TimeDelayedListener extends JobListenerSupport {

    @Override
    public String getName() {
        return "TimeDelayedListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        final JobDetail jobDetail = context.getJobDetail();
        JobDesc jobDesc = (JobDesc) jobDetail.getJobDataMap().get("jobDesc");
        final JobKey key = jobDetail.getKey();
        if (jobDesc == null) {
            throw new HodorSchedulerException("job {} jobDesc is null", key);
        }
        if (jobDesc.getTimeType() != TimeType.FIXED_DELAY) {
            return;
        }
        try {
            context.getScheduler().pauseJob(key);
        } catch (SchedulerException e) {
            throw new HodorSchedulerException("PauseJob job {} exception, {}", key, e.getMessage(), e);
        }
    }
}
