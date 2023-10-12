package org.dromara.hodor.server.executor.handler;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * job request executor
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorJobRequestHandler implements RequestHandler {

    @Override
    public void resultHandle(Map<String, Object> attachment, final RemotingResponse<JobExecuteResponse> remotingResponse) {
        HodorJobResponseHandler.INSTANCE.fireJobResponseHandler(remotingResponse);
    }

    @Override
    public void exceptionHandle(final HodorJobExecutionContext context, final Throwable t) {
        log.error("Job [key:{}, id:{}] dispatch exception, msg: {}.", context.getJobKey(), context.getRequestId(), t.getMessage(), t);
        RemotingResponse<JobExecuteResponse> errorResponse = getErrorResponse(context, t);
        HodorJobResponseHandler.INSTANCE.fireJobResponseHandler(errorResponse);
    }

    public RemotingResponse<JobExecuteResponse> getErrorResponse(final HodorJobExecutionContext context, final Throwable t) {
        JobExecuteResponse jobExecuteResponse = new JobExecuteResponse();
        jobExecuteResponse.setRequestId(context.getRequestId());
        jobExecuteResponse.setInstanceId(context.getInstanceId());
        jobExecuteResponse.setJobKey(context.getJobKey());
        jobExecuteResponse.setCompleteTime(DateUtil.formatDateTime(new Date()));
        jobExecuteResponse.setStatus(JobExecuteStatus.ERROR);
        jobExecuteResponse.setComments(ThreadUtils.getStackTraceInfo(t));
        return RemotingResponse.failed("InnerError", jobExecuteResponse);
    }

}
