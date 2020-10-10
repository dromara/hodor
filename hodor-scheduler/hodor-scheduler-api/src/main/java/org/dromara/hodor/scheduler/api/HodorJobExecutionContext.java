package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.common.IdGenerator;
import org.dromara.hodor.core.JobDesc;

/**
 *  hodor scheduler context
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public class HodorJobExecutionContext {

    private final long requestId;
    private final String jobKey;
    private final JobDesc jobDesc;

    public HodorJobExecutionContext(JobDesc jobDesc) {
        this.requestId = IdGenerator.defaultGenerator().nextId();
        this.jobDesc = jobDesc;
        this.jobKey = jobDesc.getGroupName() + "_" + jobDesc.getJobName();
    }

    public JobDesc getJobDesc() {
        return jobDesc;
    }

    public long getRequestId() {
        return requestId;
    }

    public String getJobKey() {
        return jobKey;
    }

}
