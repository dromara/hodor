package org.dromara.hodor.storage.exception;

import org.dromara.hodor.common.exception.HodorException;

/**
 * StorageException
 *
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class StorageException extends HodorException {

    private static final long serialVersionUID = 3977252363774092620L;

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
