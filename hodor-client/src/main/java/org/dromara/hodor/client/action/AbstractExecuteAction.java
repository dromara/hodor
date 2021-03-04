package org.dromara.hodor.client.action;

import cn.hutool.core.date.DateUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import org.apache.logging.log4j.Logger;
import org.draomara.hodor.model.executor.JobExecuteStatus;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.annotation.HodorProperties;
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

    private final HodorProperties properties;

    public AbstractExecuteAction(RequestContext context) {
        super(context);
        this.properties = ServiceProvider.getInstance().getBean(HodorProperties.class);
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
        File jobLoggerFile = new File(createLogPath(request), createLogFileName(request));
        loggerName = createLoggerName(request);
        jobLogger = LogUtil.getInstance().createLogger(loggerName, jobLoggerFile);

        // executing job
        jobLogger.info("start executing job.");

        ScheduledResponse remotingResponse = executeRequest0(request);

        jobLogger.info("job execution completed.");
        return remotingResponse;
    }

    public String createLogPath(ScheduledRequest request) {
        return getJobLoggerDir() + File.separator + request.getRequestId();
    }

    public void sendStartExecuteResponse(ScheduledRequest request) {
        ScheduledResponse response = buildResponse(request);
        response.setStatus(JobExecuteStatus.RUNNING);
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
        return System.getProperty("user.dir", properties.getRootJobLogPath());
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
