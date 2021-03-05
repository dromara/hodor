package org.dromara.hodor.client.action;

import org.draomara.hodor.model.executor.JobExecuteStatus;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.executor.ExecutorManager;
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

    public KillRunningJobAction(RequestContext context) {
        super(context);
        this.executorManager = ExecutorManager.getInstance();
    }

    @Override
    public KillRunningJobResponse executeRequest(KillRunningJobRequest request) throws Exception {
        Thread runningThread = executorManager.getRunningThread(request.getRequestId());
        KillRunningJobResponse response = new KillRunningJobResponse();
        if (runningThread == null) {
            response.setStatus(JobExecuteStatus.FINISHED);
            return response;
        }

        runningThread.interrupt();

        response.setStatus(JobExecuteStatus.KILLED);
        return response;
    }

}
