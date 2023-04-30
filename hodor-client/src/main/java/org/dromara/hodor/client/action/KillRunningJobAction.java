package org.dromara.hodor.client.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.dromara.hodor.client.core.HodorJobExecution;
import org.dromara.hodor.client.executor.ExecutorManager;
import org.dromara.hodor.client.executor.JobExecutionPersistence;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;

/**
 * 停止正在运行的任务
 *
 * @author tomgs
 * @since 1.0
 */
public class KillRunningJobAction extends AbstractAction<KillRunningJobRequest, KillRunningJobResponse> {

    private final ExecutorManager executorManager;

    private final JobExecutionPersistence jobExecutionPersistence;

    public KillRunningJobAction(final RequestContext context, final JobExecutionPersistence jobExecutionPersistence) {
        super(context);
        this.executorManager = ExecutorManager.getInstance();
        this.jobExecutionPersistence = jobExecutionPersistence;
    }

    @Override
    public KillRunningJobResponse executeRequest(KillRunningJobRequest request) {
        KillRunningJobResponse response = new KillRunningJobResponse();
        response.setCompleteTime(DateUtil.formatDateTime(new Date()));
        response.setStatus(JobExecuteStatus.KILLED);

        Thread runningThread = executorManager.getRunningThread(request.getRequestId());
        if (runningThread == null) {
            response.setStatus(JobExecuteStatus.FINISHED);
            return response;
        }

        runningThread.interrupt();

        HodorJobExecution killedJobExecution = HodorJobExecution.createKilledJobExecution(request.getRequestId());
        jobExecutionPersistence.fireJobExecutionEvent(killedJobExecution);
        return response;
    }

}
