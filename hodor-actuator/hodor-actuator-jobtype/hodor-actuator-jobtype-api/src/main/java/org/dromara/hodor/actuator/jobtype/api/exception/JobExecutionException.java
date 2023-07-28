package org.dromara.hodor.actuator.jobtype.api.exception;

/**
 * JobExecutionException
 *
 * @author tomgs
 * @since 1.0
 **/
public class JobExecutionException extends RuntimeException {

    private static final long serialVersionUID = -4816318363684461696L;

    public JobExecutionException(final String errorMessage, final Exception e, final Object... args) {
        super(String.format(errorMessage, args), e);
    }

    public JobExecutionException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    public JobExecutionException(final Throwable cause) {
        super(cause);
    }
}
