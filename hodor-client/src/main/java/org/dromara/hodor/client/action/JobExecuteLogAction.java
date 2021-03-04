package org.dromara.hodor.client.action;

import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteLogResponse;

/**
 * 查询任务执行日志
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class JobExecuteLogAction extends AbstractAction<JobExecuteLogRequest, JobExecuteLogResponse> {

    public JobExecuteLogAction(RequestContext context) {
        super(context);
    }

    @Override
    public JobExecuteLogResponse executeRequest(JobExecuteLogRequest request) throws Exception {
        return null;
    }

}
