package org.dromara.hodor.actuator.api.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.HodorJobExecution;
import org.dromara.hodor.actuator.api.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.api.executor.RequestHandleManager;
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

    private final JobExecutionPersistence jobExecutionPersistence;

    public KillRunningJobAction(final RequestContext context,
                                final JobExecutionPersistence jobExecutionPersistence,
                                final RequestHandleManager requestHandleManager) {
        super(context, requestHandleManager);
        this.jobExecutionPersistence = jobExecutionPersistence;
    }

    @Override
    public KillRunningJobResponse executeRequest(KillRunningJobRequest request) throws Exception {
        KillRunningJobResponse response = new KillRunningJobResponse();
        response.setCompleteTime(DateUtil.formatDateTime(new Date()));
        response.setStatus(JobExecuteStatus.KILLED);

        ExecutableJobContext executableJobContext = getRequestHandleManager().getExecutableJobContext(request.getRequestId());
        if (executableJobContext == null) {
            response.setStatus(JobExecuteStatus.FINISHED);
            return response;
        }

        executableJobContext.getJobRunnable().stop(executableJobContext);

        HodorJobExecution killedJobExecution = HodorJobExecution.createKilledJobExecution(request.getRequestId());
        jobExecutionPersistence.fireJobExecutionEvent(killedJobExecution);
        return response;
    }

}
