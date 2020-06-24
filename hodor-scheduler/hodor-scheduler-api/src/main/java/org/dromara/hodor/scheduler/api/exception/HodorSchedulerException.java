package org.dromara.hodor.scheduler.api.exception;

/**
 * @author tangzy
 * @since 1.0
 */
public class HodorSchedulerException extends RuntimeException {

    public HodorSchedulerException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    public HodorSchedulerException(final Throwable cause) {
        super(cause);
    }

}
