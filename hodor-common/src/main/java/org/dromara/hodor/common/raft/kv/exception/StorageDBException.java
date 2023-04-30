package org.dromara.hodor.common.raft.kv.exception;

/**
 * StorageDBException
 *
 * @author tomgs
 * @since 1.0
 */
public class StorageDBException extends RuntimeException {

    private static final long serialVersionUID = 778869658680719207L;

    public StorageDBException(String message) {
        super(message);
    }

    public StorageDBException(String message, Throwable cause) {
        super(message, cause);
    }

}
