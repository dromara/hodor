package org.dromara.hodor.client.action;

import java.io.File;
import java.text.MessageFormat;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.remoting.api.message.response.RemotingResponse;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.remoting.api.message.request.SchedulerRequestBody;
import org.dromara.hodor.client.executor.ExecutorManager;
import org.dromara.hodor.common.log.LogUtil;

/**
 * abstract execute action
 *
 * @author tomgs
 * @since 2021/3/2
 */
public abstract class AbstractExecuteAction extends AbstractAction {

    private String loggerName;

    private Logger jobLogger;

    private Long requestId;

    public AbstractExecuteAction(RequestContext context) {
        super(context);
    }

    public abstract RemotingResponse executeRequest(SchedulerRequestBody requestBody) throws Exception;

    @Override
    public void execute() throws Exception {
        // deserialization
        SchedulerRequestBody requestBody = buildRequestMessage(SchedulerRequestBody.class);
        requestId = requestBody.getRequestId();

        // send start execute response
        RemotingResponse response = new RemotingResponse(requestId, 1, "start executing job.");
        sendMessage(buildResponseMessage(response));

        // log current thread
        ExecutorManager.getInstance().addRunningThread(requestId, Thread.currentThread());

        // create job logger
        String logPath = getJobLoggerDir() + File.separator + requestBody.getRequestId();
        File jobLoggerFile = new File(logPath, createLogFileName(requestBody));
        loggerName = createLoggerName(requestBody);
        jobLogger = LogUtil.getInstance().createLogger(loggerName, jobLoggerFile);

        // executing job
        jobLogger.info("start executing job.");
        RemotingResponse remotingResponse = executeRequest(requestBody);
        remotingResponse.setRequestId(requestId);

        // send complete execute response
        sendMessage(buildResponseMessage(remotingResponse));
        jobLogger.info("job execution completed.");
    }

    @Override
    public void exceptionCaught(Exception e) {
        jobLogger.error("execute job has exception, {}.", e.getMessage(), e);
        // send failed execute response
        RemotingResponse response = new RemotingResponse(requestId, 1, "failed", e);
        sendMessage(buildResponseMessage(response));
    }

    public Logger getLogger() {
        return this.jobLogger;
    }

    private String getJobLoggerDir() {
        return System.getProperty("user.dir");
    }

    private String createLoggerName(SchedulerRequestBody requestBody) {
        return MessageFormat.format("{0}_{1}_{2}_{3}", System.currentTimeMillis(),
            requestBody.getGroupName(),
            requestBody.getJobName(),
            requestBody.getRequestId());
    }

    private String createLogFileName(SchedulerRequestBody requestBody) {
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
