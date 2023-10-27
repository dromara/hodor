package org.dromara.hodor.server.executor.handler;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.core.Constants;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.manager.JobOperatorManager;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
        JobExecuteResponse responseData = remotingResponse.getData();
        Optional.ofNullable(attachment.get(Constants.JobConstants.TIME_TYPE_KEY))
            .ifPresent(e ->
                rescheduleJob(TimeType.ofName(e.toString()), responseData.getJobKey()));
    }

    private static void rescheduleJob(TimeType timeType, JobKey jobKey) {
        JobDesc jobDesc = JobDesc.builder()
            .hashId(HashUtils.hash(jobKey.getGroupName() + jobKey.getJobName()))
            .groupName(jobKey.getGroupName())
            .jobName(jobKey.getJobName())
            .timeType(timeType)
            .build();
        rescheduleFixedDelayJob(jobDesc);
    }

    @Override
    public void exceptionHandle(final HodorJobExecutionContext context, final Throwable t) {
        log.error("Job [key:{}, id:{}] dispatch exception, msg: {}.", context.getJobKey(), context.getRequestId(), t.getMessage(), t);
        RemotingResponse<JobExecuteResponse> errorResponse = getErrorResponse(context, t);
        HodorJobResponseHandler.INSTANCE.fireJobResponseHandler(errorResponse);

        rescheduleFixedDelayJob(context.getJobDesc());
    }

    private static void rescheduleFixedDelayJob(JobDesc jobDesc) {
        if (TimeType.FIXED_DELAY != jobDesc.getTimeType()) {
            return;
        }
        JobOperatorManager jobOperatorManager = ServiceProvider.getInstance().getBean(JobOperatorManager.class);
        jobOperatorManager.reschedule(jobDesc);
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
