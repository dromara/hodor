package org.dromara.hodor.client.model;

import lombok.Data;

/**
 * 杀死任务的请求
 * @author superlit
 * @create 2023/7/28 16:18
 */
@Data
public class KillJobRequest {
    private Long requestId;

    private String groupName;

    private String jobName;

    private Integer timeout;

    private String actuatorEndpoint;
}
