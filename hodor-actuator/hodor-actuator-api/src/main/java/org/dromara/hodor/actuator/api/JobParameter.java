package org.dromara.hodor.actuator.api;

import lombok.Getter;
import lombok.ToString;

/**
 * job parameter
 *
 * @author tomgs
 * @since 1.0
 */
@Getter
@ToString
public class JobParameter {

    private final String groupName;

    private final String jobName;

    private final Long requestId;

    private final String parameters;

    private final Integer shardingCount;

    private final Integer shardingId;

    public JobParameter(String groupName, String jobName, Long requestId, String parameters, Integer shardingCount, Integer shardingId) {
        this.groupName = groupName;
        this.jobName = jobName;
        this.requestId = requestId;
        this.parameters = parameters;
        this.shardingCount = shardingCount;
        this.shardingId = shardingId;
    }

}
