package org.dromara.hodor.remoting.api.message.response;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 *  
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class KillRunningJobResponse implements ResponseBody {

    private static final long serialVersionUID = 6568410342583057026L;

    private Long requestId;

    private int status;

    @Override
    public Long getRequestId() {
        return requestId;
    }

    @Override
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
