package org.dromara.hodor.model.job;

import lombok.Data;

import java.util.Objects;

/**
 * job key
 *
 * @author tomgs
 * @since 1.0
 */
@Data
public class JobKey {

    private String groupName;

    private String jobName;

    private String command;

    public JobKey(String groupName, String jobName, String command) {
        this.groupName = groupName;
        this.jobName = jobName;
        this.command = command;
    }

    public static JobKey of(String groupName, String jobName) {
        return new JobKey(groupName, jobName, null);
    }

    public static JobKey of(String groupName, String jobName, String command) {
        return new JobKey(groupName, jobName, command);
    }

    public static JobKey of(String jobKey) {
        String[] split = jobKey.split("#");
        if (split.length != 2 && split.length != 3) {
            throw new IllegalArgumentException(String.format("jobKey [%s] is illegal.", jobKey));
        }
        if (split.length == 2) {
            return new JobKey(split[0], split[1], null);
        } else {
            return new JobKey(split[0], split[1], split[2]);
        }
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
        return Objects.equals(groupName, jobKey.groupName)
            && Objects.equals(jobName, jobKey.jobName)
            && Objects.equals(command, jobKey.command);
    }

    public String getKeyName() {
        return this.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, jobName, command);
    }

    @Override
    public String toString() {
        if (command == null) {
            return String.format("%s#%s", groupName, jobName);
        } else {
            return String.format("%s#%s#%s", groupName, jobName, command);
        }
    }

}
