package org.dromara.hodor.server.executor.handler;

/**
 * ResponseHandler
 *
 * @author tomgs
 * @since 1.0
 */
public interface ResponseHandler<T> {

    void fireJobResponseHandler(T response);

}
