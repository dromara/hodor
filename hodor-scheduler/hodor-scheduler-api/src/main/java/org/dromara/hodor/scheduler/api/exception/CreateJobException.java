package org.dromara.hodor.scheduler.api.exception;

/**
 *  CreateJobException
 *
 * @author tomgs
 * @version 2021/8/7 1.0 
 */
public class CreateJobException extends RuntimeException {

    public CreateJobException(String message) {
        super(message);
    }

    public CreateJobException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateJobException(Throwable cause) {
        super(cause);
    }

    protected CreateJobException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
