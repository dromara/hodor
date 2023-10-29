package org.dromara.hodor.actuator.api.action;

import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.HodorJobExecution;
import org.dromara.hodor.actuator.api.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.api.executor.RequestHandleManager;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.common.utils.Utils.Dates;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteStatusResponse;

/**
 * 查询任务执行状态
 *
 * @author tomgs
 * @since 1.0
 */
public class JobExecuteStatusAction extends AbstractAction<JobExecuteStatusRequest, JobExecuteStatusResponse> {

    private final JobExecutionPersistence jobExecutionPersistence;

    public JobExecuteStatusAction(final RequestContext context,
                                  final JobExecutionPersistence jobExecutionPersistence,
                                  final RequestHandleManager requestHandleManager) {
        super(context, requestHandleManager);
        this.jobExecutionPersistence = jobExecutionPersistence;
    }

    @Override
    public JobExecuteStatusResponse executeRequest(JobExecuteStatusRequest request) throws Exception {
        JobExecuteStatusResponse response = new JobExecuteStatusResponse();
        HodorJobExecution jobExecution = jobExecutionPersistence.fetchJobExecution(request.getRequestId());
        if (jobExecution == null) {
            ExecutableJobContext context = getRequestHandleManager().getExecutableJobContext(request.getRequestId());
            Assert.notNull(context, "not found status info with requestId {}", request.getRequestId());
            response.setRequestId(context.getRequestId());
            response.setStatus(context.getExecuteStatus());
            return response;
        }
        response.setStatus(jobExecution.getStatus());
        response.setStartTime(Dates.formatDateTime(jobExecution.getStartTime()));
        response.setCompleteTime(Dates.formatDateTime(jobExecution.getCompleteTime()));
        response.setComments(jobExecution.getComments());
        response.setResult(jobExecution.getResult());
        return response;
    }

}
