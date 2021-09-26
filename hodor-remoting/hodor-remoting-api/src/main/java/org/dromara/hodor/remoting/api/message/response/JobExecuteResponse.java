package org.dromara.hodor.remoting.api.message.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;

/**
 * scheduled response
 *
 * @author tomgs
 * @since 2021/2/26
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class JobExecuteResponse extends AbstractResponseBody {

    private static final long serialVersionUID = 8889407473710885893L;

    private JobKey jobKey;

    private JobExecuteStatus status;

    private String startTime;

    private String completeTime;

    private Long processTime;

    private Integer shardId;

    private String shardName;

    private String comments;

    private byte[] result;

    @Override
    public String toString() {
        return "JobExecuteResponse{" +
            "jobKey=" + jobKey +
            ", status=" + status +
            ", startTime='" + startTime + '\'' +
            ", completeTime='" + completeTime + '\'' +
            ", processTime=" + processTime +
            ", shardId=" + shardId +
            ", shardName='" + shardName + '\'' +
            ", comments='" + comments + '\'' +
            ", result='" + StringUtils.decodeString(result) + '\'' +
            '}';
    }

}
