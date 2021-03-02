package org.dromara.hodor.client.action;

import org.dromara.hodor.client.JobExecutionContext;
import org.dromara.hodor.client.JobParameter;
import org.dromara.hodor.client.JobRegistrar;
import org.dromara.hodor.remoting.api.message.response.RemotingResponse;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.core.ScheduledMethodRunnable;
import org.dromara.hodor.remoting.api.message.request.SchedulerRequestBody;

/**
 * job execution
 *
 * @author tomgs
 * @since 2021/3/2
 */
public class JobExecuteAction extends AbstractExecuteAction {

    private final JobRegistrar jobRegistrar;

    public JobExecuteAction(RequestContext context) {
        super(context);
        this.jobRegistrar = ServiceProvider.getInstance().getBean(JobRegistrar.class);
    }

    @Override
    public RemotingResponse executeRequest(SchedulerRequestBody requestBody) {
        ScheduledMethodRunnable jobRunnable = jobRegistrar.getJobRunnable(requestBody.getGroupName(), requestBody.getJobName());
        if (jobRunnable == null) {
            throw new IllegalArgumentException(String.format("not found job %s_%s.", requestBody.getGroupName(), requestBody.getJobName()));
        }
        if (jobRunnable.isHasArg()) {
            final JobParameter jobParameter = new JobParameter(requestBody.getGroupName(), requestBody.getJobName(), requestBody.getRequestId(), requestBody.getJobParameters());
            final JobExecutionContext context = new JobExecutionContext(getLogger(), jobParameter);
            jobRunnable.setArgs(context);
        }

        jobRunnable.run();
        // to set job return result to response
        return new RemotingResponse(0, "success");
    }

}
