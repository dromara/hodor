package org.dromara.hodor.server.manager;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.executor.job.JobExecuteRecorder;

/**
 * job status manager
 *
 * @author tomgs
 * @since 2021/8/10
 */
public enum JobExecuteStatusManager {
    INSTANCE;

    private final JobExecuteRecorder jobExecuteRecorder = ServiceProvider.getInstance().getBean(JobExecuteRecorder.class);

    public static JobExecuteStatusManager getInstance() {
        return INSTANCE;
    }

    public boolean isRunning(String jobKey) {
        return false;
    }
    public void addRunningJob(HodorJobExecutionContext context, Host host) {
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_INSERT, buildRunningJobExecDetail(context, host));
    }

    public void addFailureJob(HodorJobExecutionContext context, Throwable t) {
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_UPDATE,
            buildFailureJobExecDetail(context, t));
        removeRunningJob(context);
    }

    public void addFinishJob(JobExecuteResponse jobExecuteResponse) {
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_UPDATE,
            buildFinishJobExecDetail(jobExecuteResponse));
    }

    public void removeRunningJob(HodorJobExecutionContext context) {

    }

    private JobExecDetail buildRunningJobExecDetail(HodorJobExecutionContext context, Host host) {
        JobDesc jobDesc = context.getJobDesc();
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(context.getRequestId());
        jobExecDetail.setGroupName(jobDesc.getGroupName());
        jobExecDetail.setJobName(jobDesc.getJobName());
        jobExecDetail.setScheduleStart(new Date());
        jobExecDetail.setSchedulerEndpoint(StringUtils.splitToList(context.getSchedulerName(), "_").get(1));
        jobExecDetail.setActuatorEndpoint(host.getEndpoint());
        jobExecDetail.setExecuteStatus(JobExecuteStatus.PENDING);
        return null;
    }

    private JobExecDetail buildFailureJobExecDetail(HodorJobExecutionContext context, Throwable t) {
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(context.getRequestId());
        jobExecDetail.setExecuteStatus(JobExecuteStatus.ERROR);
        jobExecDetail.setComments(ThreadUtils.getStackTraceInfo(t));
        return jobExecDetail;
    }

    private JobExecDetail buildFinishJobExecDetail(JobExecuteResponse response) {
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(response.getRequestId());
        jobExecDetail.setExecuteStatus(response.getStatus());
        jobExecDetail.setExecuteStart(DateUtil.parse(response.getStartTime(), DatePattern.NORM_DATETIME_FORMAT));
        jobExecDetail.setExecuteEnd(DateUtil.parse(response.getCompleteTime(), DatePattern.NORM_DATETIME_FORMAT));
        jobExecDetail.setElapsedTime(response.getProcessTime());
        jobExecDetail.setComments(response.getComments());
        return jobExecDetail;
    }

}
