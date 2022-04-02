package org.dromara.hodor.common.raft.kv.exception;

/**
 * HodorKVClientException
 *
 * @author tomgs
 * @since 2022/3/24
 */
public class HodorKVClientException extends RuntimeException {

    private static final long serialVersionUID = 6950362941569207560L;

    public HodorKVClientException(String message) {
        super(message);
    }

    public HodorKVClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
