package org.dromara.hodor.remoting.api.message.response;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 *  job execute log response
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class JobExecuteLogResponse implements ResponseBody {

    private static final long serialVersionUID = -8444222502655435905L;

    @Override
    public Long getRequestId() {
        return null;
    }

    @Override
    public void setRequestId(Long requestId) {

    }

}
