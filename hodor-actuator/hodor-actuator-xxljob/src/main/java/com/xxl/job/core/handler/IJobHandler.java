package com.xxl.job.core.handler;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.dromara.hodor.actuator.api.ExecutableJob;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;

/**
 * job handler
 *
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler implements ExecutableJob {

    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @throws Exception execute exception
     */
    public abstract void execute() throws Exception;

    /**
     * init handler, invoked when JobThread init
     */
    public void init() throws Exception {
        // do something
    }

    /**
     * destroy handler, invoked when JobThread destroy
     */
    public void destroy() throws Exception {
        // do something
    }

    @Override
    public Object execute(ExecutableJobContext jobContext) throws Exception {
        final JobLogger jobLogger = jobContext.getJobLogger();
        final Integer timeout = jobContext.getExecuteRequest().getTimeout();
        // destroy
        try {
            jobLogger.info(">>>>>>>>>>> xxl-job JobThread init, jobId:{}, requestId:{}",
                jobContext.getJobKey(), jobContext.getRequestId());
            init();

            jobLogger.info(">>>>>>>>>>> xxl-job JobThread start, jobId:{}, requestId:{}",
                jobContext.getJobKey(), jobContext.getRequestId());
            XxlJobContext xxlJobContext = convert2XxlJobContext(jobContext);
            XxlJobContext.setXxlJobContext(xxlJobContext);

            if (timeout > 0) {
                // limit timeout
                Thread futureThread = null;
                try {
                    FutureTask<Boolean> futureTask = new FutureTask<>(() -> {
                        // init job context
                        XxlJobContext.setXxlJobContext(xxlJobContext);
                        execute();
                        return true;
                    });
                    futureThread = new Thread(futureTask);
                    futureThread.start();
                    Boolean tempResult = futureTask.get(timeout, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    XxlJobHelper.log("<br>----------- xxl-job job execute timeout");
                    XxlJobHelper.log(e);
                    // handle result
                    XxlJobHelper.handleTimeout("job execute timeout ");
                } finally {
                    futureThread.interrupt();
                }
            } else {
                // just execute
                execute();
            }

            final int handleCode = xxlJobContext.getHandleCode();
            final String handleMsg = xxlJobContext.getHandleMsg();
            if (XxlJobContext.HANDLE_CODE_SUCCESS != handleCode) {

            }
        } finally {
            destroy();
            XxlJobContext.removeXxlJobContext();
        }
        jobLogger.info(">>>>>>>>>>> xxl-job JobThread stoped, jobId:{}, requestId:{}",
            jobContext.getJobKey(), jobContext.getRequestId());
        return null;
    }

    @Override
    public void stop(ExecutableJobContext jobContext) throws Exception {
        Thread runningThread = jobContext.getCurrentThread();
        if (runningThread == null) {
            jobContext.getJobLogger().info("not found running job {}", jobContext.getJobKey());
            return;
        }
        runningThread.interrupt();
        jobContext.setExecuteStatus(JobExecuteStatus.KILLED);
    }

    public static XxlJobContext convert2XxlJobContext(ExecutableJobContext jobContext) {
        final JobLogger jobLogger = jobContext.getJobLogger();
        final JobExecuteRequest executeRequest = jobContext.getExecuteRequest();
        int shardingId = 0;
        if (executeRequest.getShardingId() != null) {
            shardingId = executeRequest.getShardingId();
        }

        return new XxlJobContext(jobContext.getRequestId(), executeRequest.getJobParameters(), jobLogger, null,
            shardingId, executeRequest.getShardingCount());
    }

}
