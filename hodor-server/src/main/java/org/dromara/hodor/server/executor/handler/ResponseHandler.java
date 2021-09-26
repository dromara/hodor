package org.dromara.hodor.server.executor.handler;

/**
 * ResponseHandler
 *
 * @author tomgs
 * @since 2021/9/8
 */
public interface ResponseHandler<T> {

    void fireJobResponseHandler(T response);

}
