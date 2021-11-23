package org.dromara.hodor.actuator.common.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

/**
 * job execution
 *
 * @author tomgs
 * @since 2021/3/2
 */
public class JobExecuteAction extends AbstractExecuteAction {

    public JobExecuteAction(final RequestContext context,
                            final HodorProperties properties,
                            final JobExecutionPersistence jobExecutionPersistence,
                            final RequestHandleManager requestHandleManager) {
        super(context, properties, jobExecutionPersistence, requestHandleManager);
    }

    @Override
    public JobExecuteResponse executeRequest0(final JobExecuteRequest request) {

        // to set job return result to response
        JobExecuteResponse response = buildResponse(request);
        response.setStatus(JobExecuteStatus.SUCCEEDED);
        response.setCompleteTime(DateUtil.formatDateTime(new Date()));
        /*if (result != null) {
            response.setResult(getRequestContext().serializer().serialize(result));
        }*/
        return response;
    }

}
