package org.dromara.hodor.client.action;

import java.io.File;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.config.HodorProperties;
import org.dromara.hodor.client.core.JobLoggerManager;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.utils.FileIOUtils;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteLogResponse;

/**
 * 查询任务执行日志
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class JobExecuteLogAction extends AbstractAction<JobExecuteLogRequest, JobExecuteLogResponse> {

    private final JobLoggerManager jobLoggerManager;

    private final HodorProperties properties;

    public JobExecuteLogAction(RequestContext context) {
        super(context);
        this.jobLoggerManager = JobLoggerManager.getInstance();
        this.properties = ServiceProvider.getInstance().getBean(HodorProperties.class);
    }

    @Override
    public JobExecuteLogResponse executeRequest(JobExecuteLogRequest request) throws Exception {
        File jobLoggerFile = jobLoggerManager.buildJobLoggerFile(properties.getRootJobLogPath(), request.getGroupName(), request.getJobName(), request.getRequestId());
        FileIOUtils.LogData logData = FileIOUtils.readUtf8File(jobLoggerFile, request.getOffset(), request.getLength());

        JobExecuteLogResponse response = new JobExecuteLogResponse();
        response.setOffset(logData.getOffset());
        response.setLength(logData.getLength());
        response.setData(logData.getData());
        return response;
    }

}
