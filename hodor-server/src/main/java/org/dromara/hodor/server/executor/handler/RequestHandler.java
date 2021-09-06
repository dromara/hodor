package org.dromara.hodor.server.executor.handler;

import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * RequestHandler
 *
 * @author tomgs
 * @since 2021/9/6
 */
public interface RequestHandler {

    void preHandle(HodorJobExecutionContext context);

    void handle(HodorJobExecutionContext context);

    void postHandle(HodorJobExecutionContext context);

    void exceptionCaught(final HodorJobExecutionContext context, final Throwable t);

}
