package org.dromara.hodor.remoting.api.message.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *  kill running job response
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class KillRunningJobResponse extends AbstractResponseBody {

    private static final long serialVersionUID = 6568410342583057026L;

    private int status;

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
