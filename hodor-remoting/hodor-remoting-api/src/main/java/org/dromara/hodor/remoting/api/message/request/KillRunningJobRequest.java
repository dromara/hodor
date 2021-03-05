package org.dromara.hodor.remoting.api.message.request;

import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 *  
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class KillRunningJobRequest implements RequestBody {

    private static final long serialVersionUID = -1814909263364679439L;

    private Long requestId;

    @Override
    public Long getRequestId() {
        return requestId;
    }

    @Override
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

}
