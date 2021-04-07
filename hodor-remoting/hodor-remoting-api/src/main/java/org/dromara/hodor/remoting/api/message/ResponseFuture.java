package org.dromara.hodor.remoting.api.message;

import java.rmi.RemoteException;

/**
 * response future
 *
 * @author tomgs
 * @since 2021/4/7
 */
public interface ResponseFuture {

    RemotingMessage get() throws RemoteException;

    RemotingMessage get(long timeout) throws RemoteException;

    /**
     * Is done boolean.
     *
     * @return the boolean
     */
    boolean isDone();

    /**
     * Returns {@code true} if and only if the I/O operation was completed
     * successfully.
     */
    boolean isSuccess();

    /**
     * Cause boolean.
     *
     * @return the boolean
     */
    Throwable cause();

    void receive(RemotingMessage responseMessage);

}
