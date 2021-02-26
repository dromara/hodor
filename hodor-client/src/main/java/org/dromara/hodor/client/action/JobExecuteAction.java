package org.dromara.hodor.client.action;

import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.core.SchedulerRequestBody;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;

/**
 * job execute action
 *
 * @author tomgs
 * @since 2021/2/26
 */
public class JobExecuteAction extends AbstractAction {

    private final RemotingMessageSerializer serializer;

    public JobExecuteAction(final RequestContext context) {
        super(context);
        this.serializer = ServiceProvider.getInstance().getBean(RemotingMessageSerializer.class);
    }

    @Override
    public void execute() throws Exception {
        RequestContext requestContext = getRequestContext();
        SchedulerRequestBody requestBody = serializer.deserialize(requestContext.rawRequestBody(), SchedulerRequestBody.class);

    }

    @Override
    public void exceptionCaught(Exception e) {

    }

}
