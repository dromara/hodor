package org.dromara.hodor.client.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.draomara.hodor.model.executor.JobExecuteStatus;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.core.HodorJobExecution;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.executor.ExecutorManager;
import org.dromara.hodor.client.executor.JobPersistence;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;

/**
 * 停止正在运行的任务
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class KillRunningJobAction extends AbstractAction<KillRunningJobRequest, KillRunningJobResponse> {

    private final ExecutorManager executorManager;

    private final JobPersistence jobPersistence;

    public KillRunningJobAction(final RequestContext context) {
        super(context);
        this.executorManager = ExecutorManager.getInstance();
        this.jobPersistence = ServiceProvider.getInstance().getBean(JobPersistence.class);
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
        jobPersistence.fireJobExecutionEvent(killedJobExecution);
        return response;
    }

}
