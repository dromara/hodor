package org.dromara.hodor.scheduler.api;

import java.util.Date;
import org.dromara.hodor.common.IdGenerator;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;

/**
 *  hodor scheduler context
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public class HodorJobExecutionContext {

    private final long requestId;
    private final String schedulerName;
    private final JobKey jobKey;
    private final JobDesc jobDesc;
    private final Date fireTime;

    public HodorJobExecutionContext(final JobDesc jobDesc,
                                    final String schedulerName,
                                    final Date fireTime) {
        this.requestId = IdGenerator.defaultGenerator().nextId();
        this.schedulerName = schedulerName;
        this.jobDesc = jobDesc;
        this.jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        this.fireTime = fireTime;
    }

    public long getRequestId() {
        return requestId;
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public JobDesc getJobDesc() {
        return jobDesc;
    }

    public Date getFireTime() {
        return fireTime;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

}
