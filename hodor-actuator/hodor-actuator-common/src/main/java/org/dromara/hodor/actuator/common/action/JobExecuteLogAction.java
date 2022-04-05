package org.dromara.hodor.actuator.common.action;

import java.nio.file.Path;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.actuator.common.utils.JobPathUtils;
import org.dromara.hodor.common.utils.FileIOUtils;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteLogResponse;

/**
 * 查询任务执行日志
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class JobExecuteLogAction extends AbstractAction<JobExecuteLogRequest, JobExecuteLogResponse> {

    private final HodorProperties properties;

    public JobExecuteLogAction(final RequestContext context,
                               final HodorProperties properties,
                               final RequestHandleManager requestHandleManager) {
        super(context, requestHandleManager);
        this.properties = properties;
    }

    @Override
    public JobExecuteLogResponse executeRequest(JobExecuteLogRequest request) throws Exception {
        Path jobLogPath = JobPathUtils.getJobLogPath(properties.getDataPath(),
            JobKey.of(request.getGroupName(), request.getJobName()),
            request.getRequestId());
        FileIOUtils.LogData logData = FileIOUtils.readUtf8File(jobLogPath.toFile(), request.getOffset(), request.getLength());

        JobExecuteLogResponse response = new JobExecuteLogResponse();
        response.setOffset(logData.getOffset());
        response.setLength(logData.getLength());
        response.setData(logData.getData());
        return response;
    }

}
