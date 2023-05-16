package org.dromara.hodor.admin.exception;

public class ValidationException extends Exception {

    private static final long serialVersionUID = 5786424522311331310L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }


}
