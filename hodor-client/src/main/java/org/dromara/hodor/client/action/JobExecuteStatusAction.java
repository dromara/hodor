package org.dromara.hodor.client.action;

import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteStatusResponse;

/**
 * 查询任务执行状态
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class JobExecuteStatusAction extends AbstractAction<JobExecuteStatusRequest, JobExecuteStatusResponse> {

    public JobExecuteStatusAction(RequestContext context) {
        super(context);
    }

    @Override
    public JobExecuteStatusResponse executeRequest(JobExecuteStatusRequest request) throws Exception {
        return null;
    }

}
