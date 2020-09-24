package org.dromara.hodor.remoting.api.message;

import java.io.Serializable;

/**
 * request body
 *
 * @author tomgs
 * @since 2020/9/15
 */
public interface RequestBody extends Serializable {

    /**
     * 请求id
     * @return request id
     */
    String getRequestId();

}
