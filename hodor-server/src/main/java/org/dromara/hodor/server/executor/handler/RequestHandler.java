package org.dromara.hodor.server.executor.handler;

import java.util.Map;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * RequestHandler
 *
 * @author tomgs
 * @since 1.0
 */
public interface RequestHandler {

    void resultHandle(Map<String, Object> attachment, final RemotingResponse<JobExecuteResponse> remotingResponse);

    void exceptionHandle(final HodorJobExecutionContext context, final Throwable t);

}
