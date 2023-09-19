package org.dromara.hodor.remoting.api.message;

import java.io.Serializable;

/**
 * request body
 *
 * @author tomgs
 * @since 1.0
 */
public interface RequestBody extends Serializable {

    /**
     * request id
     * @return request id
     */
    Long getRequestId();

    /**
     * set request id
     * @param requestId request id
     */
    void setRequestId(Long requestId);

}
