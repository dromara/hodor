package org.dromara.hodor.remoting.api;

import java.util.EventListener;

/**
 * Listens to the result of a {@link HodorChannelFuture}.  The result of the asynchronous operation is notified once this listener
 * is added by calling {@link HodorChannelFuture#operationComplete}.
 */
public interface HodorChannelFutureListener<F extends HodorChannelFuture> extends EventListener {

    /**
     * Invoked when the operation associated with the {@link HodorChannelFuture} has been completed.
     *
     * @param future  the source {@link HodorChannelFuture} which called this callback
     */
    void operationComplete(F future) throws Exception;
}
