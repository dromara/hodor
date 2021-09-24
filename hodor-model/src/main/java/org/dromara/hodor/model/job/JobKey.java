package org.dromara.hodor.model.job;

import lombok.Data;

import java.util.Objects;

/**
 * job key
 *
 * @author tomgs
 * @since 2021/8/13
 */
@Data
public class JobKey {

    private String groupName;

    private String jobName;

    public JobKey(String groupName, String jobName) {
        this.groupName = groupName;
        this.jobName = jobName;
    }

    public static JobKey of(String groupName, String jobName) {
        return new JobKey(groupName, jobName);
    }

    public static JobKey of(String jobKey) {
        String[] split = jobKey.split("#", 2);
        if (split.length != 2) {
            throw new IllegalArgumentException(String.format("jobKey [%s] is illegal.", jobKey));
        }
        return new JobKey(split[0], split[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobKey jobKey = (JobKey) o;
        return Objects.equals(groupName, jobKey.groupName) && Objects.equals(jobName, jobKey.jobName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, jobName);
    }

    @Override
    public String toString() {
        return String.format("%s#%s", groupName, jobName);
    }

    public String getKeyName() {
        return this.toString();
    }

}
