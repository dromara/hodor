package org.dromara.hodor.actuator.common.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.common.JobRegister;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.core.ExecutableJobContext;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.common.executor.JobExecutionPersistence;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.common.utils.StringUtils;
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

    private final JobRegister jobRegister;

    public JobExecuteAction(final RequestContext context,
                            final HodorProperties properties,
                            final JobExecutionPersistence jobExecutionPersistence,
                            final JobRegister jobRegister,
                            final RequestHandleManager requestHandleManager) {
        super(context, properties, jobExecutionPersistence, requestHandleManager);
        this.jobRegister = jobRegister;
    }

    @Override
    public JobExecuteResponse executeRequest0(final JobExecuteRequest request) throws Exception {
        final JobKey jobKey = JobKey.of(request.getGroupName(), request.getJobName());
        final ExecutableJobContext executableJobContext = buildExecutableJobContext(request, jobKey);
        final JobRunnable runnableJob = buildJobRunnable(executableJobContext);
        final Logger log = getJobLogger().getLogger();

        Object result;
        for (int i = 0; ; i++) {
            log.info("execute job, attempts: {}", i);
            try {
                executableJobContext.setExecuteStatus(JobExecuteStatus.RUNNING);
                result = runnableJob.execute(executableJobContext);
                executableJobContext.setExecuteStatus(JobExecuteStatus.SUCCEEDED);
                break;
            } catch (Exception e) {
                log.error("execute job exception, attempts:{} , msg: {}", i, e.getMessage(), e);
                if (i == request.getRetryCount()) {
                    executableJobContext.setExecuteStatus(JobExecuteStatus.FAILED);
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

    private JobRunnable buildJobRunnable(ExecutableJobContext executableJobContext) throws Exception {
        final JobRunnable runnableJob = jobRegister.getRunnableJob(executableJobContext);
        if (runnableJob == null) {
            throw new JobExecutionException(StringUtils.format("not found job {}.", executableJobContext.getJobKey()));
        }
        executableJobContext.setJobRunnable(runnableJob);
        return runnableJob;
    }

    private ExecutableJobContext buildExecutableJobContext(JobExecuteRequest request, JobKey jobKey) {
        final ExecutableJobContext executableJobContext = getRequestHandleManager().getExecutableJobContext(request.getRequestId());
        executableJobContext.setJobKey(jobKey);
        executableJobContext.setExecuteRequest(request);
        executableJobContext.setJobCommandType(request.getJobCommandType());
        executableJobContext.setCurrentThread(Thread.currentThread());
        executableJobContext.setExecuteStatus(JobExecuteStatus.PENDING);
        executableJobContext.setJobLogger(getJobLogger());
        executableJobContext.setRequestContext(getRequestContext());
        return executableJobContext;
    }

}
