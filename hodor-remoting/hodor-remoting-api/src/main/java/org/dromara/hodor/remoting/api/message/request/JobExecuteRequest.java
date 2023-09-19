package org.dromara.hodor.remoting.api.message.request;

import lombok.Builder;
import lombok.Data;
import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 *  request body
 *
 * @author tomgs
 * @version 2020/9/13 1.0
 */
@Data
@Builder
public class JobExecuteRequest implements RequestBody {

    private static final long serialVersionUID = -3703044901986185064L;

    private Long requestId;

    private String jobName;

    private String groupName;

    private String jobPath;

    private String jobCommand;

    private String jobCommandType;

    private String jobParameters;

    private String extensibleParameters;

    @Builder.Default
    private Integer timeout = 180;

    @Builder.Default
    private Integer shardingCount = 0;

    @Builder.Default
    private Integer shardingId = 0;

    @Builder.Default
    private Integer retryCount = 0;

    @Builder.Default
    private Integer version = -1;
}

