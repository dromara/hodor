package org.dromara.hodor.common.exception;

/**
 *  Indicates that a required property is missing from the Props
 *
 * @author tomgs
 * @version 2020/6/26 1.0 
 */
public class UndefinedPropertyException extends RuntimeException {

    private static final long serialVersionUID = 1;

    public UndefinedPropertyException(final String message) {
        super(message);
    }

}
