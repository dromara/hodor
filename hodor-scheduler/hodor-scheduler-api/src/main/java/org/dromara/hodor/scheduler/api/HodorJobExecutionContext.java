package org.dromara.hodor.scheduler.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Getter
@Setter
@ToString
public class HodorJobExecutionContext {

    private Long instanceId;

    private Long requestId;

    private String schedulerName;

    private JobKey rootJobKey;

    private JobKey jobKey;

    private JobDesc jobDesc;

    private Date fireTime;

    private Integer shardingCount = 1;

    private Integer shardingId = 0;

    private String shardingParams;

    private final List<Host> hosts = new ArrayList<>();

    public HodorJobExecutionContext(final JobKey rootJobKey, final JobDesc jobDesc,
                                    final String schedulerName, final Date fireTime) {
        this.rootJobKey = rootJobKey;
        this.instanceId = IdGenerator.defaultGenerator().nextId();
        this.requestId = IdGenerator.defaultGenerator().nextId();
        this.schedulerName = schedulerName;
        this.jobDesc = jobDesc;
        this.jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        this.fireTime = fireTime;
    }

    public HodorJobExecutionContext(final long instanceId, final long requestId,
                                    final JobKey rootJobKey, final JobDesc jobDesc,
                                    final String schedulerName, final Date fireTime) {
        this.instanceId = instanceId;
        this.requestId = requestId;
        this.schedulerName = schedulerName;
        this.jobDesc = jobDesc;
        this.jobKey = JobKey.of(jobDesc.getGroupName(), jobDesc.getJobName());
        this.rootJobKey = rootJobKey;
        this.fireTime = fireTime;
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

}
