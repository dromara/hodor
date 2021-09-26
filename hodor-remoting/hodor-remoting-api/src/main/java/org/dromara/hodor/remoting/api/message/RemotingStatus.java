package org.dromara.hodor.remoting.api.message;

/**
 * remoting status
 *
 * @author tomgs
 * @since 2021/3/4
 */
public interface RemotingStatus {

    /**
     * 正常响应
     */
    int SUCCEEDED = 0;

    /**
     * 异常响应
     */
    int FAILED = 1;

}
