package org.dromara.hodor.remoting.api.message.response;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 *  heartbeat response
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class HeartbeatResponse implements ResponseBody {

    private static final long serialVersionUID = -6628660201359842320L;

    @Override
    public Long getRequestId() {
        return null;
    }

    @Override
    public void setRequestId(Long requestId) {

    }

}
