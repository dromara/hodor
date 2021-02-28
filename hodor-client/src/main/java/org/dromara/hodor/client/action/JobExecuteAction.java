package org.dromara.hodor.client.action;

import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.RemotingResponse;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.core.SchedulerRequestBody;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * job execute action
 *
 * @author tomgs
 * @since 2021/2/26
 */
public class JobExecuteAction extends AbstractAction {

    public JobExecuteAction(final RequestContext context) {
        super(context);
    }

    @Override
    public void execute() throws Exception {
        SchedulerRequestBody requestBody = buildRequestMessage(SchedulerRequestBody.class);

        Class<?> jobClass = Class.forName(requestBody.getJobPath());

        Object bean = ServiceProvider.getInstance().getBean(jobClass);
        String jobCommand = requestBody.getJobCommand();
        String jobParameters = requestBody.getJobParameters();

        //Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());

        RemotingResponse responseBody = new RemotingResponse(0, "success");
        RemotingMessage response = buildResponseMessage(responseBody);
        sendMessage(response);
    }

    @Override
    public void exceptionCaught(Exception e) {
        RemotingResponse responseBody = new RemotingResponse(1, "failure", e);
        RemotingMessage response = buildResponseMessage(responseBody);
        sendMessage(response);
    }

}
