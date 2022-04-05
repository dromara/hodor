package org.dromara.hodor.actuator.common.action;

import cn.hutool.core.date.DateUtil;
import org.dromara.hodor.actuator.common.core.HodorJobExecution;
import org.dromara.hodor.actuator.common.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteStatusResponse;

/**
 * 查询任务执行状态
 *
 * @author tomgs
 * @since 2021/3/4
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
        HodorJobExecution jobExecution = jobExecutionPersistence.fetchJobExecution(request.getRequestId());
        if (jobExecution == null) {
            throw new IllegalArgumentException(StringUtils.format("not found status info with requestId {}", request.getRequestId()));
        }
        JobExecuteStatusResponse response = new JobExecuteStatusResponse();
        response.setStatus(jobExecution.getStatus());
        response.setStartTime(DateUtil.formatDateTime(jobExecution.getStartTime()));
        response.setCompleteTime(DateUtil.formatDateTime(jobExecution.getCompleteTime()));
        response.setComments(jobExecution.getComments());
        response.setResult(jobExecution.getResult());
        return response;
    }

}
