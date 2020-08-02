package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.common.exception.UndefinedPropertyException;
import org.dromara.hodor.core.JobInfo;

import java.util.Map;

/**
 *  hodor scheduler context
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public class HodorJobExecutionContext {

    private final Map<String, Object> jobData;
    private final JobInfo jobInfo;

    public HodorJobExecutionContext(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
        this.jobData = jobInfo.getJobData();
    }

    public String getString(final String key) {
        if (jobData.containsKey(key)) {
            return String.valueOf(jobData.get(key));
        } else {
            throw new UndefinedPropertyException("Missing required property '" + key + "'");
        }
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }

}
