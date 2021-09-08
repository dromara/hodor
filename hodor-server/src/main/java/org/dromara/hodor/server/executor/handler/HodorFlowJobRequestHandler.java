package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * HodorFlowJobRequestHandler
 *
 * @author tomgs
 * @since 2021/9/8
 */
@Slf4j
public class HodorFlowJobRequestHandler extends HodorJobRequestHandler {

    @Override
    public void resultHandle(final RemotingResponse<JobExecuteResponse> remotingResponse) {
        HodorFlowJobResponseHandler.INSTANCE.fireJobResponseHandler(remotingResponse);
    }

    @Override
    public void exceptionCaught(final HodorJobExecutionContext context, final Throwable t) {
        log.error("job {} request [id:{}] execute exception, msg: {}.", context.getRequestId(), context.getJobKey(), t.getMessage(), t);
        RemotingResponse<JobExecuteResponse> errorResponse = getErrorResponse(context, t);
        HodorFlowJobResponseHandler.INSTANCE.fireJobResponseHandler(errorResponse);
    }

}
