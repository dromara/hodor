package org.dromara.hodor.remoting.api.message.response;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * abstract response body
 *
 * @author tomgs
 * @since 2021/3/16
 */
public abstract class AbstractResponseBody implements ResponseBody {

    private static final long serialVersionUID = 4227027475165007997L;

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
