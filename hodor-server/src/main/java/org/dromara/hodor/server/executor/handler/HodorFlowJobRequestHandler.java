package org.dromara.hodor.server.executor.handler;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Tuple2;
import org.dromara.hodor.core.Constants.FlowNodeConstants;
import org.dromara.hodor.model.job.JobKey;
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
    public void resultHandle(Map<String, Object> attachment, final RemotingResponse<JobExecuteResponse> remotingResponse) {
        String rootJobKey = String.valueOf(attachment.get(FlowNodeConstants.ROOT_JOB_KEY));
        HodorFlowJobResponseHandler.INSTANCE.fireJobResponseHandler(new Tuple2<>(JobKey.of(rootJobKey), remotingResponse));
    }

    @Override
    public void exceptionCaught(final HodorJobExecutionContext context, final Throwable t) {
        log.error("job {} request [id:{}] execute exception, msg: {}.", context.getRequestId(), context.getJobKey(), t.getMessage(), t);
        RemotingResponse<JobExecuteResponse> errorResponse = getErrorResponse(context, t);
        HodorFlowJobResponseHandler.INSTANCE.fireJobResponseHandler(new Tuple2<>(context.getRootJobKey(), errorResponse));
    }

}
