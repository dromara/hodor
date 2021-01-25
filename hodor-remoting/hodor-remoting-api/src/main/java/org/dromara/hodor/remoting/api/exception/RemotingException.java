package org.dromara.hodor.remoting.api.exception;

/**
 * Remoting Exception
 *
 * @author tomgs
 * @since 2020/9/15
 */
public class RemotingException extends RuntimeException {

    private static final long serialVersionUID = -6508645997078070413L;

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

}
