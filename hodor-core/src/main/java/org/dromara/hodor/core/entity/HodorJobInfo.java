package org.dromara.hodor.core.entity;

import lombok.Data;
import org.dromara.hodor.core.JobInfo;
import org.dromara.hodor.core.enums.JobStatus;
import org.dromara.hodor.core.enums.Priority;

import java.util.Map;

/**
 *  hodor job info
 *
 * @author tomgs
 * @version 2020/8/2 1.0 
 */
@Data
public class HodorJobInfo implements JobInfo {

    private Long id;

    private String groupName;

    private String jobName;

    private JobStatus jobStatus;

    @Override
    public String getJobKey() {
        return null;
    }

    @Override
    public String getGroupName() {
        return null;
    }

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Priority getPriority() {
        return null;
    }

    @Override
    public Map<String, Object> getJobData() {
        return null;
    }

    @Override
    public String getCron() {
        return null;
    }

}
