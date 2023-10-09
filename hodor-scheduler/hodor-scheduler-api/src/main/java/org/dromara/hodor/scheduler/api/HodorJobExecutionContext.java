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

    private Integer shardingCount = 1;

    private Integer shardingId = 0;

    private String shardingParams;

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

    public void refreshHosts(Host selected) {
        hosts.remove(selected);
        hosts.add(selected);
    }

    public List<Host> getHosts() {
        return this.hosts;
    }

    public Integer getShardingCount() {
        return shardingCount;
    }

    public void setShardingCount(Integer shardingCount) {
        this.shardingCount = shardingCount;
    }

    public Integer getShardingId() {
        return shardingId;
    }

    public void setShardingId(Integer shardingId) {
        this.shardingId = shardingId;
    }

    public String getShardingParams() {
        return shardingParams;
    }

    public void setShardingParams(String shardingParams) {
        this.shardingParams = shardingParams;
    }

    @Override
    public String toString() {
        return "HodorJobExecutionContext{" +
            "requestId=" + requestId +
            ", schedulerName='" + schedulerName + '\'' +
            ", rootJobKey=" + rootJobKey +
            ", jobKey=" + jobKey +
            ", jobDesc=" + jobDesc +
            ", fireTime=" + fireTime +
            ", shardingCount=" + shardingCount +
            ", shardingId=" + shardingId +
            ", shardingParams='" + shardingParams + '\'' +
            ", hosts=" + hosts +
            '}';
    }
}
