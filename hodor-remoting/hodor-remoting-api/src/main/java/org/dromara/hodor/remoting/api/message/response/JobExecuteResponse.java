package org.dromara.hodor.remoting.api.message.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.dromara.hodor.model.enums.JobExecuteStatus;

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

    private JobExecuteStatus status;

    private String startTime;

    private String completeTime;

    private Integer processTime;

    private Integer shardId;

    private String shardName;

    private String comments;

    private String result;

    @Override
    public String toString() {
        return "JobExecuteResponse{" +
            "requestId=" + getRequestId() + '\'' +
            ",status=" + status + '\'' +
            ", startTime='" + startTime + '\'' +
            ", completeTime='" + completeTime + '\'' +
            ", processTime=" + processTime +
            ", shardId=" + shardId +
            ", shardName='" + shardName + '\'' +
            ", comments='" + comments + '\'' +
            ", result='" + result + '\'' +
            '}';
    }
}
