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

    private String groupName;

    private String jobName;

    private String requestId;

    private String parameters;

    private String sharingRequestId;

    public JobParameter(String groupName, String jobName, String requestId, String parameters, String sharingRequestId) {
        this.groupName = groupName;
        this.jobName = jobName;
        this.requestId = requestId;
        this.parameters = parameters;
        this.sharingRequestId = sharingRequestId;
    }

}
