package org.dromara.hodor.server.executor.exception;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * JobScheduleException
 *
 * @author tomgs
 * @since 1.0
 */
public class JobScheduleException extends RuntimeException {

    private static final long serialVersionUID = 2744800351260711205L;

    public JobScheduleException(String message, Object...args) {
        super(StringUtils.format(message, args));
    }

    public JobScheduleException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobScheduleException(Throwable cause) {
        super(cause);
    }

}
