package org.dromara.hodor.actuator.bigdata.exception;

/**
 * @author tangzhongyuan
 * @create 2018-11-26 21:50
 **/
public class JobExecutionException extends RuntimeException {

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
