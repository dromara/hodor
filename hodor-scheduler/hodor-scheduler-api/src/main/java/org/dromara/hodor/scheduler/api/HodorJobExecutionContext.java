package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.common.IdGenerator;
import org.dromara.hodor.model.job.JobDesc;

import java.util.Date;

/**
 *  hodor scheduler context
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public class HodorJobExecutionContext {

    private final long requestId;
    private final String schedulerName;
    private final String jobKey;
    private final JobDesc jobDesc;
    private final Date fireTime;
    private final Date scheduledFireTime;
    private final Date previousFireTime;
    private final Date nextFireTime;

    public HodorJobExecutionContext(final JobDesc jobDesc,
                                    final String schedulerName,
                                    final Date fireTime,
                                    final Date scheduledFireTime,
                                    final Date previousFireTime,
                                    final Date nextFireTime) {
        this.requestId = IdGenerator.defaultGenerator().nextId();
        this.schedulerName = schedulerName;
        this.jobDesc = jobDesc;
        this.jobKey = jobDesc.getGroupName() + "_" + jobDesc.getJobName();
        this.fireTime = fireTime;
        this.scheduledFireTime = scheduledFireTime;
        this.previousFireTime = previousFireTime;
        this.nextFireTime = nextFireTime;
    }

    public long getRequestId() {
        return requestId;
    }

    public String getJobKey() {
        return jobKey;
    }

    public JobDesc getJobDesc() {
        return jobDesc;
    }

    public Date getFireTime() {
        return fireTime;
    }

    public Date getScheduledFireTime() {
        return scheduledFireTime;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

}
