package org.dromara.hodor.client.action;

import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;

/**
 * 停止正在运行的任务
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class KillRunningJobAction extends AbstractAction<KillRunningJobRequest, KillRunningJobResponse> {

    public KillRunningJobAction(RequestContext context) {
        super(context);
    }

    @Override
    public KillRunningJobResponse executeRequest(KillRunningJobRequest request) throws Exception {
        return null;
    }

}
