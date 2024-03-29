package org.dromara.hodor.server.executor.exception;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * IllegalJobExecuteStateException
 *
 * @author tomgs
 * @since 1.0
 */
public class IllegalJobExecuteStateException extends RuntimeException {

    private static final long serialVersionUID = 2744800351260711204L;

    public IllegalJobExecuteStateException(String message, Object...args) {
        super(StringUtils.format(message, args));
    }

    public IllegalJobExecuteStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalJobExecuteStateException(Throwable cause) {
        super(cause);
    }

}
