package org.dromara.hodor.scheduler.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.IdGenerator;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;

/**
 * hodor scheduler context
 *
 * @author tomgs
 * @version 2020/6/25 1.0
 */
public class HodorJobExecutionContext {

    private final long requestId;

    private final String schedulerName;

    private final JobKey rootJobKey;

    private final JobKey jobKey;

    private final JobDesc jobDesc;

    private final Date fireTime;

    private final List<Host> hosts = new ArrayList<>();

    public HodorJobExecutionContext(final JobKey rootJobKey, final JobDesc jobDesc,
                                    final String schedulerName, final Date fireTime) {
        this.rootJobKey = rootJobKey;
        this.requestId = IdGenerator.defaultGenerator().nextId();
        this.schedulerName = schedulerName;
        this.jobDesc = jobDesc;
        this.jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        this.fireTime = fireTime;
    }

    public HodorJobExecutionContext(final long requestId,
                                    final JobKey rootJobKey, final JobDesc jobDesc,
                                    final String schedulerName, final Date fireTime) {
        this.requestId = requestId;
        this.schedulerName = schedulerName;
        this.jobDesc = jobDesc;
        this.jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        this.rootJobKey = rootJobKey;
        this.fireTime = fireTime;
    }

    public long getRequestId() {
        return requestId;
    }

    public JobKey getRootJobKey() {
        return rootJobKey;
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

    public void resetHosts(List<Host> hosts) {
        if (!this.hosts.isEmpty()) {
            this.hosts.clear();
        }
        this.hosts.addAll(hosts);
    }

    public List<Host> getHosts() {
        return this.hosts;
    }

    @Override
    public String toString() {
        return "HodorJobExecutionContext {" +
            "requestId=" + requestId +
            ", schedulerName='" + schedulerName + '\'' +
            ", rootJobKey=" + rootJobKey +
            ", jobKey=" + jobKey +
            ", fireTime=" + fireTime +
            ", hosts=" + hosts +
            '}';
    }

}
