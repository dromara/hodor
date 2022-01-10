package org.dromara.hodor.actuator.common.action;

import java.io.File;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.actuator.common.core.JobLoggerManager;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.common.utils.FileIOUtils;
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

    private final JobLoggerManager jobLoggerManager;

    public JobExecuteLogAction(final RequestContext context, final HodorProperties properties, final RequestHandleManager requestHandleManager) {
        super(context, requestHandleManager);
        this.properties = properties;
        this.jobLoggerManager = JobLoggerManager.getInstance();
    }

    @Override
    public JobExecuteLogResponse executeRequest(JobExecuteLogRequest request) throws Exception {
        File jobLoggerFile = jobLoggerManager.buildJobLoggerFile(properties.getDataPath(), request.getGroupName(), request.getJobName(), request.getRequestId());
        FileIOUtils.LogData logData = FileIOUtils.readUtf8File(jobLoggerFile, request.getOffset(), request.getLength());

        JobExecuteLogResponse response = new JobExecuteLogResponse();
        response.setOffset(logData.getOffset());
        response.setLength(logData.getLength());
        response.setData(logData.getData());
        return response;
    }

}
