package org.dromara.hodor.actuator.api.action;

import cn.hutool.core.date.DateUtil;
import java.io.File;
import java.util.Date;
import java.util.Map;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.actuator.api.core.HodorJobExecution;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.actuator.api.core.JobLoggerManager;
import org.dromara.hodor.actuator.api.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.api.executor.RequestHandleManager;
import org.dromara.hodor.actuator.api.utils.JobPathUtils;
import org.dromara.hodor.common.utils.Stopwatch;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

/**
 * abstract execute action
 *
 * @author tomgs
 * @since 1.0
 */
public abstract class AbstractExecuteAction extends AbstractAction<JobExecuteRequest, JobExecuteResponse> {

    private JobLogger jobLogger;

    private Long requestId;

    private JobKey jobKey;

    private final HodorProperties properties;

    private final JobExecutionPersistence jobExecutionPersistence;

    private final Stopwatch stopwatch;

    private JobLoggerManager jobLoggerManager;

    public AbstractExecuteAction(final RequestContext context,
                                 final HodorProperties properties,
                                 final JobExecutionPersistence jobExecutionPersistence,
                                 final RequestHandleManager requestHandleManager) {
        super(context, requestHandleManager);
        this.properties = properties;
        this.jobExecutionPersistence = jobExecutionPersistence;
        this.stopwatch = Stopwatch.create();
    }

    public abstract JobExecuteResponse executeRequest0(final JobExecuteRequest request) throws Exception;

    @Override
    public JobExecuteResponse executeRequest(final JobExecuteRequest request) throws Exception {
        requestId = request.getRequestId();
        jobKey = JobKey.of(request.getGroupName(), request.getJobName());
        // create job logger
        final String loggerName = JobPathUtils.createLoggerName(request.getGroupName(), request.getJobName(), requestId);
        final File loggerFile = JobPathUtils.buildJobLoggerFile(properties.getDataPath(), request.getGroupName(), request.getJobName(), requestId);
        this.jobLoggerManager = new JobLoggerManager(loggerName, null, loggerFile.toPath());
        this.jobLogger = jobLoggerManager.createJobLogger();

        jobLogger.info("job ready.");
        // send start execute response
        sendStartExecuteResponse(request);

        // executing job
        jobLogger.info("job start executing.");

        stopwatch.start();
        JobExecuteResponse remotingResponse = executeRequest0(request);
        stopwatch.stop();

        jobLogger.info("job execution completed.");

        HodorJobExecution successJobExecution = HodorJobExecution.createSuccessJobExecution(requestId, remotingResponse.getResult());
        jobExecutionPersistence.fireJobExecutionEvent(successJobExecution);
        remotingResponse.setProcessTime(stopwatch.elapsedMillis());
        return remotingResponse;
    }

    @Override
    public void exceptionCaught(Exception e) {
        jobLogger.error("execute exception: {}", e.getMessage(), e);

        String exceptionStack = ThreadUtils.getStackTraceInfo(e);
        HodorJobExecution failureJobExecution = HodorJobExecution.createFailureJobExecution(requestId, exceptionStack);
        jobExecutionPersistence.fireJobExecutionEvent(failureJobExecution);

        JobExecuteResponse response = new JobExecuteResponse();
        response.setRequestId(requestId);
        response.setJobKey(jobKey);
        response.setStatus(JobExecuteStatus.FAILED);
        response.setCompleteTime(DateUtil.formatDateTime(new Date()));
        response.setComments(exceptionStack);
        retryableSendMessage(RemotingResponse.succeeded(response));
    }

    public void sendStartExecuteResponse(final JobExecuteRequest request) {
        JobExecuteResponse response = buildResponse(request);
        response.setStatus(JobExecuteStatus.RUNNING);
        response.setStartTime(DateUtil.formatDateTime(new Date()));

        Map<String, Object> attachment = getRequestContext().requestHeader().getAttachment();
        HodorJobExecution runningJobExecution = HodorJobExecution.createRunningJobExecution(request.getRequestId(), request.getGroupName(),
            request.getJobName(), request.getJobParameters(),
            attachment == null ? getRequestContext().channel().remoteAddress().toString() : attachment.get("schedulerName").toString());
        jobExecutionPersistence.fireJobExecutionEvent(runningJobExecution);
        retryableSendMessage(RemotingResponse.succeeded(response));
    }

    public JobExecuteResponse buildResponse(final JobExecuteRequest request) {
        JobExecuteResponse response = new JobExecuteResponse();
        response.setRequestId(request.getRequestId());
        response.setJobKey(jobKey);
        response.setShardingCount(request.getShardingCount());
        response.setShardingId(request.getShardingId());
        return response;
    }

    public JobLogger getJobLogger() {
        return this.jobLogger;
    }

    @Override
    public void afterProcess() {
        jobLogger.info("job execution finished.");
        getRequestHandleManager().removeExecutableJobContext(requestId);
        jobLoggerManager.stopJobLogger();
    }

}
