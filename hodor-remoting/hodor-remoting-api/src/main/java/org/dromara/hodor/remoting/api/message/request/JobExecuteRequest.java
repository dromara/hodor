package org.dromara.hodor.remoting.api.message.request;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.dromara.hodor.model.enums.JobExecuteStatus;
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

    private Long instanceId;

    private String jobName;

    private String groupName;

    private String jobCommandType;

    private String jobCommand;

    private String jobParameters;

    private String extensibleParameters;

    // 上游任务结果数据
    private Object parentJobData;

    //上游任务的结果 Map<requestId, result>
    private Map<Long, Object> parentJobExecuteResults;

    //上游任务的状态 Map<requestId, executeStatus>
    private Map<Long, JobExecuteStatus> parentJobExecuteStatuses;

    @Builder.Default
    private Integer timeout = 180;

    @Builder.Default
    private Integer shardingCount = 1;

    @Builder.Default
    private Integer shardingId = 0;

    private String shardingParams;

    @Builder.Default
    private Integer retryCount = 0;

    @Builder.Default
    private Integer version = 0;
}

