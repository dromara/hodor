package org.dromara.hodor.client;

import lombok.Getter;
import lombok.ToString;

/**
 * job parameter
 *
 * @author tomgs
 * @since 2021/1/4
 */
@Getter
@ToString
public class JobParameter {

    private final String groupName;

    private final String jobName;

    private final Long requestId;

    private final String parameters;

    private final Integer shardId;

    private final String shardName;

    public JobParameter(String groupName, String jobName, Long requestId, String parameters, Integer shardId, String shardName) {
        this.groupName = groupName;
        this.jobName = jobName;
        this.requestId = requestId;
        this.parameters = parameters;
        this.shardId = shardId;
        this.shardName = shardName;
    }

}
