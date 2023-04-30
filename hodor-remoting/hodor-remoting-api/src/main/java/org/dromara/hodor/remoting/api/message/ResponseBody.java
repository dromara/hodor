package org.dromara.hodor.remoting.api.message;

import java.io.Serializable;

/**
 * response body
 *
 * @author tomgs
 * @since 1.0
 */
public interface ResponseBody extends Serializable {

    /**
     * 请求id
     * @return request id
     */
    Long getRequestId();

    /**
     * 设置requestId
     * @param requestId 请求id
     */
    void setRequestId(Long requestId);

}
