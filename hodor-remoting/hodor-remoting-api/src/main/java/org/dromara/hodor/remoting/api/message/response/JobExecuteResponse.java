package org.dromara.hodor.remoting.api.message.response;

import lombok.Getter;
import lombok.Setter;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * scheduled response
 *
 * @author tomgs
 * @since 1.0
 */
@Getter
@Setter
public class JobExecuteResponse implements ResponseBody {

    private static final long serialVersionUID = 8889407473710885893L;

    private Long requestId;

    private Long instanceId;

    private JobKey jobKey;

    private JobExecuteStatus status;

    private String startTime;

    private String completeTime;

    private Long processTime;

    private Integer shardingCount;

    private Integer shardingId;

    private String shardingParams;

    private String comments;

    private byte[] result;

    @Override
    public String toString() {
        return "JobExecuteResponse{" +
            "requestId=" + requestId +
            ", instanceId=" + instanceId +
            ", jobKey=" + jobKey +
            ", status=" + status +
            ", startTime='" + startTime + '\'' +
            ", completeTime='" + completeTime + '\'' +
            ", processTime=" + processTime +
            ", shardingCount=" + shardingCount +
            ", shardingId=" + shardingId +
            ", shardingParams='" + shardingParams + '\'' +
            ", comments='" + comments + '\'' +
            ", result='" + StringUtils.decodeString(result) + '\'' +
            '}';
    }
}
