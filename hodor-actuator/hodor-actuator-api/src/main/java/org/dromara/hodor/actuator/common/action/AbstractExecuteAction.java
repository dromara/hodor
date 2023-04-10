package org.dromara.hodor.actuator.common.action;

import cn.hutool.core.date.DateUtil;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.core.HodorJobExecution;
import org.dromara.hodor.actuator.common.core.JobLogger;
import org.dromara.hodor.actuator.common.core.JobLoggerManager;
import org.dromara.hodor.actuator.common.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.common.utils.Stopwatch;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * abstract execute action
 *
 * @author tomgs
 * @since 2021/3/2
 */
public abstract class AbstractExecuteAction extends AbstractAction<JobExecuteRequest, JobExecuteResponse> {

    private String loggerName;

    private JobLogger jobLogger;

    private Long requestId;

    private JobKey jobKey;

    private final HodorProperties properties;

    private final JobExecutionPersistence jobExecutionPersistence;

    private final JobLoggerManager jobLoggerManager;

    private final Stopwatch stopwatch;

    public AbstractExecuteAction(final RequestContext context,
                                 final HodorProperties properties,
                                 final JobExecutionPersistence jobExecutionPersistence,
                                 final RequestHandleManager requestHandleManager) {
        super(context, requestHandleManager);
        this.properties = properties;
        this.jobExecutionPersistence = jobExecutionPersistence;
        this.jobLoggerManager = JobLoggerManager.getInstance();
        this.stopwatch = Stopwatch.create();
    }

    public abstract JobExecuteResponse executeRequest0(final JobExecuteRequest request) throws Exception;

    @Override
    public JobExecuteResponse executeRequest(final JobExecuteRequest request) throws Exception {
        requestId = request.getRequestId();
        jobKey = JobKey.of(request.getGroupName(), request.getJobName());
        // create job logger
        File jobLoggerFile = jobLoggerManager.buildJobLoggerFile(properties.getDataPath(), request.getGroupName(), request.getJobName(), requestId);
        this.loggerName = jobLoggerManager.createLoggerName(request.getGroupName(), request.getJobName(), requestId);
        this.jobLogger = jobLoggerManager.createJobLogger(this.loggerName, jobLoggerFile);

        jobLogger.getLogger().info("job ready.");
        // send start execute response
        sendStartExecuteResponse(request);

        // executing job
        jobLogger.getLogger().info("job start executing.");

        stopwatch.start();
        JobExecuteResponse remotingResponse = executeRequest0(request);
        stopwatch.stop();

        jobLogger.getLogger().info("job execution completed.");

        HodorJobExecution successJobExecution = HodorJobExecution.createSuccessJobExecution(requestId, remotingResponse.getResult());
        jobExecutionPersistence.fireJobExecutionEvent(successJobExecution);
        remotingResponse.setProcessTime(stopwatch.elapsedMillis());
        return remotingResponse;
    }

    @Override
    public void exceptionCaught(Exception e) {
        jobLogger.getLogger().error("execute exception: {}", e.getMessage(), e);

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
        response.setShardId(request.getShardId());
        response.setShardName(request.getShardName());
        return response;
    }

    public JobLogger getJobLogger() {
        return this.jobLogger;
    }

    @Override
    public void afterProcess() {
        jobLogger.getLogger().info("job execution finished.");
        getRequestHandleManager().removeExecutableJobContext(requestId);
        jobLoggerManager.stopJobLogger(loggerName);
    }

}
