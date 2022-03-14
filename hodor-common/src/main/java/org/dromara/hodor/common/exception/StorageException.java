package org.dromara.hodor.common.exception;

/**
 * StorageException
 *
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class StorageException extends HodorException {

    public StorageException(Throwable e) {
        super(e);
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
