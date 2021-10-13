package org.dromara.hodor.actuator.java.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.actuator.common.JobExecutionContext;
import org.dromara.hodor.actuator.common.JobParameter;
import org.dromara.hodor.actuator.common.JobRegistrar;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.core.ScheduledMethodRunnable;
import org.dromara.hodor.actuator.common.executor.JobExecutionPersistence;
import org.dromara.hodor.common.utils.ThreadUtils;
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

    private final JobRegistrar jobRegistrar;

    public JobExecuteAction(final RequestContext context, final HodorProperties properties,
                            final JobExecutionPersistence jobExecutionPersistence, final JobRegistrar jobRegistrar) {
        super(context, properties, jobExecutionPersistence);
        this.jobRegistrar = jobRegistrar;
    }

    @Override
    public JobExecuteResponse executeRequest0(final JobExecuteRequest request) {
        ScheduledMethodRunnable jobRunnable = jobRegistrar.getJobRunnable(request.getGroupName(), request.getJobName());
        if (jobRunnable == null) {
            throw new IllegalArgumentException(String.format("not found job %s_%s.", request.getGroupName(), request.getJobName()));
        }
        if (jobRunnable.isHasArg()) {
            final JobParameter jobParameter = new JobParameter(request.getGroupName(), request.getJobName(), request.getRequestId(),
                request.getJobParameters(), request.getShardId(), request.getShardName());
            final JobExecutionContext context = new JobExecutionContext(getLogger(), jobParameter);
            jobRunnable.setArgs(context);
        }

        Object result;
        for (int i = 0; ; i++) {
            getLogger().info("execute job, attempts: {}", i);
            try {
                jobRunnable.run();
                result = jobRunnable.getResult();
                break;
            } catch (Exception e) {
                getLogger().error("execute job exception, attempts:{} , msg: {}", i, e.getMessage(), e);
                if (i == request.getRetryCount()) {
                    // 异常统一处理
                    throw e;
                }
            } finally {
                jobRunnable.refresh();
            }
            ThreadUtils.sleep(TimeUnit.MILLISECONDS, 500);
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
