package org.dromara.hodor.client.action;

import cn.hutool.core.date.DateUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.executor.ExecutorManager;
import org.dromara.hodor.common.log.LogUtil;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.request.ScheduledRequest;
import org.dromara.hodor.remoting.api.message.response.ScheduledResponse;

/**
 * abstract execute action
 *
 * @author tomgs
 * @since 2021/3/2
 */
public abstract class AbstractExecuteAction extends AbstractAction<ScheduledRequest, ScheduledResponse> {

    private String loggerName;

    private Logger jobLogger;

    private Long requestId;

    public AbstractExecuteAction(RequestContext context) {
        super(context);
    }

    public abstract ScheduledResponse executeRequest0(ScheduledRequest request) throws Exception;

    @Override
    public ScheduledResponse executeRequest(ScheduledRequest request) throws Exception {
        requestId = request.getRequestId();
        // send start execute response
        sendStartExecuteResponse(request);

        // log current thread
        ExecutorManager.getInstance().addRunningThread(requestId, Thread.currentThread());

        // create job logger
        String logPath = getJobLoggerDir() + File.separator + request.getRequestId();
        File jobLoggerFile = new File(logPath, createLogFileName(request));
        loggerName = createLoggerName(request);
        jobLogger = LogUtil.getInstance().createLogger(loggerName, jobLoggerFile);

        // executing job
        jobLogger.info("start executing job.");

        ScheduledResponse remotingResponse = executeRequest0(request);

        jobLogger.info("job execution completed.");
        return remotingResponse;
    }

    public void sendStartExecuteResponse(ScheduledRequest request) {
        ScheduledResponse response = buildResponse(request);
        response.setStatus(1);
        response.setStartTime(DateUtil.formatDateTime(new Date()));
        sendMessage(buildResponseMessage(RemotingResponse.succeeded(requestId, response)));
    }

    public ScheduledResponse buildResponse(ScheduledRequest request) {
        ScheduledResponse response = new ScheduledResponse();
        response.setRequestId(request.getRequestId());
        response.setShardId(request.getShardId());
        response.setShardName(request.getShardName());
        return response;
    }

    public Logger getLogger() {
        return this.jobLogger;
    }

    private String getJobLoggerDir() {
        return System.getProperty("user.dir");
    }

    private String createLoggerName(ScheduledRequest requestBody) {
        return MessageFormat.format("{0}_{1}_{2}_{3}", System.currentTimeMillis(),
            requestBody.getGroupName(),
            requestBody.getJobName(),
            requestBody.getRequestId());
    }

    private String createLogFileName(ScheduledRequest requestBody) {
        return MessageFormat.format("_job.{0}.{1}.{2}.log",
            requestBody.getGroupName(),
            requestBody.getJobName(),
            requestBody.getRequestId());
    }

    @Override
    public void afterProcess() {
        ExecutorManager.getInstance().removeRunningThread(requestId);
        LogUtil.getInstance().stopLogger(loggerName);
    }

}
