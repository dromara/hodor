package org.dromara.hodor.core.entity;

import org.dromara.hodor.core.JobInfo;
import org.dromara.hodor.core.enums.Priority;

import java.util.Map;

/**
 *  hodor job info
 *
 * @author tomgs
 * @version 2020/8/2 1.0 
 */
public class HodorJobInfo implements JobInfo {

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
