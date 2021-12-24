package org.dromara.hodor.actuator.common.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.actuator.common.JobExecutionContext;
import org.dromara.hodor.actuator.common.JobParameter;
import org.dromara.hodor.actuator.common.JobRegistrar;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.common.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
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

    private final JobRegistrar jobRegistrar;

    public JobExecuteAction(final RequestContext context,
                            final HodorProperties properties,
                            final JobExecutionPersistence jobExecutionPersistence,
                            final JobRegistrar jobRegistrar,
                            final RequestHandleManager requestHandleManager) {
        super(context, properties, jobExecutionPersistence, requestHandleManager);
        this.jobRegistrar = jobRegistrar;
    }

    @Override
    public JobExecuteResponse executeRequest0(final JobExecuteRequest request) throws Exception {
        JobKey jobKey = JobKey.of(request.getGroupName(), request.getJobName());
        JobRunnable runnableJob = jobRegistrar.getRunnableJob(jobKey);
        if (runnableJob == null) {
            throw new JobExecutionException(String.format("not found job %s", jobKey));
        }

        final JobParameter jobParameter = new JobParameter(request.getGroupName(), request.getJobName(), request.getRequestId(),
            request.getJobParameters(), request.getShardId(), request.getShardName());
        final JobExecutionContext context = new JobExecutionContext(getLogger(), jobParameter);

        Object result;
        for (int i = 0; ; i++) {
            getLogger().info("execute job, attempts: {}", i);
            try {
                result = runnableJob.execute(context);
                break;
            } catch (Exception e) {
                getLogger().error("execute job exception, attempts:{} , msg: {}", i, e.getMessage(), e);
                if (i == request.getRetryCount()) {
                    // 异常统一处理
                    throw e;
                }
            }
        }

        // to set job return result to response
        JobExecuteResponse response = buildResponse(request);
        response.setStatus(JobExecuteStatus.SUCCEEDED);
        response.setCompleteTime(DateUtil.formatDateTime(new Date()));
        if (result != null) {
            response.setResult(getRequestContext().serializer().serialize(result));
        }
        return response;
    }

}
