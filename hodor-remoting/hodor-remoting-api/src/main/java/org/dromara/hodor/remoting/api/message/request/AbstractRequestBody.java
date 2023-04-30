package org.dromara.hodor.remoting.api.message.request;

import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 * abstract request body
 *
 * @author tomgs
 * @since 1.0
 */
public abstract class AbstractRequestBody implements RequestBody {

    private static final long serialVersionUID = -3888648975481173115L;

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
