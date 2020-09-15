package org.dromara.hodor.remoting.netty.rpc;

import java.io.Serializable;

/**
 * response body
 *
 * @author tomgs
 * @since 2020/9/15
 */
public interface ResponseBody extends Serializable {

    /**
     * 请求响应code
     * @return code
     */
    int getCode();

    /**
     * 请求响应信息
     * @return msg
     */
    String getMsg();

    /**
     * 请求id
     * @return request id
     */
    String getRequestId();

}
