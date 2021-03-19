package org.dromara.hodor.client.action;

import cn.hutool.core.date.DateUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.draomara.hodor.model.executor.JobExecuteStatus;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.annotation.HodorProperties;
import org.dromara.hodor.client.core.HodorJobExecution;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.executor.ExecutorManager;
import org.dromara.hodor.client.executor.JobPersistence;
import org.dromara.hodor.common.log.LogUtil;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

/**
 * abstract execute action
 *
 * @author tomgs
 * @since 2021/3/2
 */
public abstract class AbstractExecuteAction extends AbstractAction<JobExecuteRequest, JobExecuteResponse> {

    private String loggerName;

    private Logger jobLogger;

    private Long requestId;

    private final HodorProperties properties;

    private final JobPersistence jobPersistence;

    public AbstractExecuteAction(RequestContext context) {
        super(context);
        this.properties = ServiceProvider.getInstance().getBean(HodorProperties.class);
        this.jobPersistence = ServiceProvider.getInstance().getBean(JobPersistence.class);
    }

    public abstract JobExecuteResponse executeRequest0(JobExecuteRequest request) throws Exception;

    @Override
    public JobExecuteResponse executeRequest(JobExecuteRequest request) throws Exception {
        requestId = request.getRequestId();
        // create job logger
        File jobLoggerFile = new File(createLogPath(request), createLogFileName(request));
        loggerName = createLoggerName(request);
        jobLogger = LogUtil.getInstance().createLogger(loggerName, jobLoggerFile);

        jobLogger.info("job ready.");
        // send start execute response
        sendStartExecuteResponse(request);
        // log current thread
        ExecutorManager.getInstance().addRunningThread(requestId, Thread.currentThread());

        // executing job
        jobLogger.info("job start executing.");
        JobExecuteResponse remotingResponse = executeRequest0(request);

        jobLogger.info("job execution completed.");

        HodorJobExecution successJobExecution = HodorJobExecution.createSuccessJobExecution(requestId, remotingResponse.getResult());
        jobPersistence.fireJobExecutionEvent(successJobExecution);

        return remotingResponse;
    }

    @Override
    public void exceptionCaught(Exception e) {
        jobLogger.error("execute exception: {}", e.getMessage(), e);

        String exceptionStack = ThreadUtils.getStackTraceInfo(e);
        HodorJobExecution failureJobExecution = HodorJobExecution.createFailureJobExecution(requestId, exceptionStack);
        jobPersistence.fireJobExecutionEvent(failureJobExecution);
        super.exceptionCaught(e);
    }

    public String createLogPath(JobExecuteRequest request) {
        return getJobLoggerDir() + File.separator + request.getRequestId();
    }

    public void sendStartExecuteResponse(JobExecuteRequest request) {
        JobExecuteResponse response = buildResponse(request);
        response.setStatus(JobExecuteStatus.RUNNING);
        response.setStartTime(DateUtil.formatDateTime(new Date()));

        Map<String, Object> attachment = getRequestContext().requestHeader().getAttachment();
        HodorJobExecution runningJobExecution = HodorJobExecution.createRunningJobExecution(request.getRequestId(), request.getGroupName(),
            request.getJobName(), request.getJobParameters(),
            attachment == null ? getRequestContext().channel().remoteAddress().toString() : attachment.get("schedulerName").toString());
        jobPersistence.fireJobExecutionEvent(runningJobExecution);
        retryableSendMessage(buildResponseMessage(RemotingResponse.succeeded(requestId, response)));
    }

    public JobExecuteResponse buildResponse(JobExecuteRequest request) {
        JobExecuteResponse response = new JobExecuteResponse();
        response.setRequestId(request.getRequestId());
        response.setShardId(request.getShardId());
        response.setShardName(request.getShardName());
        return response;
    }

    public Logger getLogger() {
        return this.jobLogger;
    }

    private String getJobLoggerDir() {
        return System.getProperty("user.dir", properties.getRootJobLogPath());
    }

    private String createLoggerName(JobExecuteRequest request) {
        return MessageFormat.format("{0}_{1}_{2}_{3}", System.currentTimeMillis(),
            request.getGroupName(),
            request.getJobName(),
            request.getRequestId());
    }

    private String createLogFileName(JobExecuteRequest request) {
        return MessageFormat.format("_job.{0}.{1}.{2}.log",
            request.getGroupName(),
            request.getJobName(),
            request.getRequestId());
    }

    @Override
    public void afterProcess() {
        ExecutorManager.getInstance().removeRunningThread(requestId);
        LogUtil.getInstance().stopLogger(loggerName);
    }

}
