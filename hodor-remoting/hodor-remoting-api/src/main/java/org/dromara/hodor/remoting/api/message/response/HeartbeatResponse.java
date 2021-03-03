package org.dromara.hodor.remoting.api.message.response;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 *  heartbeat response
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class HeartbeatResponse implements ResponseBody {

    @Override
    public Long getRequestId() {
        return null;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getMsg() {
        return null;
    }

    @Override
    public Object getData() {
        return null;
    }

}
