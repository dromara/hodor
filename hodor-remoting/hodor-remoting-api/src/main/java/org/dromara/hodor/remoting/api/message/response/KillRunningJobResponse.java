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

    @Override
    public Long getRequestId() {
        return null;
    }

    @Override
    public void setRequestId(Long requestId) {

    }

}
