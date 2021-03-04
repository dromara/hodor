package org.dromara.hodor.remoting.api.message.response;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 *  job execute status response
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class JobExecuteStatusResponse implements ResponseBody {

    private static final long serialVersionUID = 8892404430523254103L;

    @Override
    public Long getRequestId() {
        return null;
    }

    @Override
    public void setRequestId(Long requestId) {

    }

}
