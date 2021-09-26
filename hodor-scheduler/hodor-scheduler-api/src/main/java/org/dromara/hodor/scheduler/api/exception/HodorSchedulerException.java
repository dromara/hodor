package org.dromara.hodor.scheduler.api.exception;

/**
 * hodor scheduler exception
 *
 * @author tangzy
 * @since 1.0
 */
public class HodorSchedulerException extends RuntimeException {

    private static final long serialVersionUID = -1050044090033256795L;

    public HodorSchedulerException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    public HodorSchedulerException(final Throwable cause) {
        super(cause);
    }

}
