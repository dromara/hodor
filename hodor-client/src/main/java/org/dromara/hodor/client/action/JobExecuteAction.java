package org.dromara.hodor.client.action;

import java.io.File;
import java.text.MessageFormat;

import org.apache.logging.log4j.Logger;
import org.dromara.hodor.client.JobExecutionContext;
import org.dromara.hodor.client.JobParameter;
import org.dromara.hodor.client.JobRegistrar;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.RemotingResponse;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.core.ScheduledMethodRunnable;
import org.dromara.hodor.client.core.SchedulerRequestBody;
import org.dromara.hodor.common.log.LogUtil;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * job execute action
 *
 * @author tomgs
 * @since 2021/2/26
 */
public class JobExecuteAction extends AbstractAction {

    private static final String DEFAULT_EXECUTION_DIR = System.getProperty("user.dir");

    private final JobRegistrar jobRegistrar;

    public JobExecuteAction(final RequestContext context) {
        super(context);
        this.jobRegistrar = ServiceProvider.getInstance().getBean(JobRegistrar.class);
    }

    @Override
    public void execute() {
        SchedulerRequestBody requestBody = buildRequestMessage(SchedulerRequestBody.class);
        ScheduledMethodRunnable jobRunnable = jobRegistrar.getJobRunnable(requestBody.getGroupName(), requestBody.getJobName());
        if (jobRunnable == null) {
            throw new IllegalArgumentException(String.format("not found job %s-%s.", requestBody.getGroupName(), requestBody.getJobName()));
        }
        if (jobRunnable.isHasArg()) {
            JobParameter jobParameter = new JobParameter(requestBody.getGroupName(), requestBody.getJobName(), requestBody.getRequestId(), requestBody.getJobParameters());

            String logPath = DEFAULT_EXECUTION_DIR + File.separator + requestBody.getRequestId();
            File jobLoggerFile = new File(logPath, createLogFileName(requestBody));
            String loggerName = createLoggerName(requestBody);
            Logger jobLogger = LogUtil.getInstance().createLogger(loggerName, jobLoggerFile);

            JobExecutionContext context = new JobExecutionContext(jobLogger, jobParameter);
            jobRunnable.setArgs(context);
        }

        jobRunnable.run();

        RemotingResponse responseBody = new RemotingResponse(0, "success");
        RemotingMessage response = buildResponseMessage(responseBody);
        sendMessage(response);
    }

    @Override
    public void exceptionCaught(Exception e) {
        RemotingResponse responseBody = new RemotingResponse(1, "failed", e);
        RemotingMessage response = buildResponseMessage(responseBody);
        sendMessage(response);
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

}
