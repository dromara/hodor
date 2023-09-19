package org.dromara.hodor.remoting.api.message.response;

import lombok.Getter;
import lombok.Setter;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 *  kill running job response
 *
 * @author tomgs
 * @version 2021/3/3 1.0
 */
@Getter
@Setter
public class KillRunningJobResponse implements ResponseBody {

    private static final long serialVersionUID = 6568410342583057026L;

    private Long requestId;

    private JobExecuteStatus status;

    private String completeTime;

    @Override
    public String toString() {
        return "KillRunningJobResponse{" +
            "requestId=" + getRequestId() + '\'' +
            ",status=" + status + '\'' +
            ", completeTime='" + completeTime + '\'' +
            '}';
    }

}
