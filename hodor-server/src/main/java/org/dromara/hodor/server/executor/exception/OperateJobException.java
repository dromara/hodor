package org.dromara.hodor.server.executor.exception;

/**
 * @author tomgs
 * @version 1.0
 */
public class OperateJobException extends RuntimeException {

    public OperateJobException(String message) {
        super(message);
    }

    public OperateJobException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperateJobException(Throwable cause) {
        super(cause);
    }

    protected OperateJobException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
