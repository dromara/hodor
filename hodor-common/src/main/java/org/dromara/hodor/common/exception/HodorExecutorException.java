package org.dromara.hodor.common.exception;

/**
 * hodor executor exception
 *
 * @author tomgs
 * @since 2021/3/18
 */
public class HodorExecutorException extends RuntimeException {

    private static final long serialVersionUID = -1979970512414865347L;

    public HodorExecutorException(String message) {
        super(message);
    }

    public HodorExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HodorExecutorException(Throwable cause) {
        super(cause);
    }

}
