package org.dromara.hodor.server.manager;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.RemotingClient;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteStatusResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.executor.job.JobExecuteRecorder;

/**
 * job status manager
 *
 * @author tomgs
 * @since 2021/8/10
 */
@Slf4j
public enum JobExecuteStatusManager {
    INSTANCE;

    private final JobExecuteRecorder jobExecuteRecorder = ServiceProvider.getInstance().getBean(JobExecuteRecorder.class);

    private final RemotingMessageSerializer serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();

    private final TypeReference<RemotingResponse<JobExecuteStatusResponse>> typeReference = new TypeReference<RemotingResponse<JobExecuteStatusResponse>>() {
    };

    public static JobExecuteStatusManager getInstance() {
        return INSTANCE;
    }

    public boolean isRunning(JobKey jobKey) {
        JobExecDetail jobExecDetail = jobExecuteRecorder.getJobExecDetail(jobKey);
        if (jobExecDetail == null
            || jobExecDetail.getExecuteStatus() != JobExecuteStatus.PENDING
            || jobExecDetail.getExecuteStatus() != JobExecuteStatus.RUNNING) {
            return false;
        }
        // 否则去执行端查询状态
        Host host = Host.of(jobExecDetail.getActuatorEndpoint());
        JobExecuteStatusRequest statusRequest = new JobExecuteStatusRequest();
        statusRequest.setRequestId(jobExecDetail.getId());
        JobExecuteStatusResponse statusResponse = queryExecuteJobStatus(host, statusRequest);
        return statusResponse != null && statusResponse.getStatus() == JobExecuteStatus.RUNNING;
    }

    public void removeRunningJob(JobKey jobKey) {
        jobExecuteRecorder.removeRunningJob(jobKey);
    }

    public void addSchedulerStartJob(HodorJobExecutionContext context) {
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_INSERT, buildSchedulerStartJobExecDetail(context));
    }

    public void addSchedulerEndJob(HodorJobExecutionContext context, Host host) {
        JobExecDetail jobExecDetail = buildSchedulerEndJobExecDetail(context, host);
        jobExecuteRecorder.addSchedulerRunningJob(jobExecDetail);
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_UPDATE, jobExecDetail);
    }

    public void addFinishJob(JobExecuteResponse jobExecuteResponse) {
        removeRunningJob(jobExecuteResponse.getJobKey());
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_UPDATE,
            buildFinishJobExecDetail(jobExecuteResponse));
    }

    public JobExecuteStatusResponse queryExecuteJobStatus(Host host, JobExecuteStatusRequest request) {
        byte[] body = serializer.serialize(request);
        Header header = Header.builder()
            .id(request.getRequestId())
            .type(MessageType.FETCH_JOB_STATUS_REQUEST.getCode())
            .length(body.length)
            .version(RemotingConst.DEFAULT_VERSION)
            .build();
        RemotingMessage remotingRequest = RemotingMessage.builder()
            .header(header)
            .body(body)
            .build();
        try {
            RemotingMessage remotingMessage = RemotingClient.getInstance().sendSyncRequest(host, remotingRequest, 1500);
            RemotingResponse<JobExecuteStatusResponse> remotingResponse =
                serializer.deserialize(remotingMessage.getBody(), typeReference.getType());
            if (remotingResponse.isSuccess()) {
                return remotingResponse.getData();
            }
        } catch (Exception e) {
            // ignore
            log.error("query job status error, msg: {}.", e.getMessage(), e);
        }
        return null;
    }

    private JobExecDetail buildSchedulerStartJobExecDetail(HodorJobExecutionContext context) {
        JobDesc jobDesc = context.getJobDesc();
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(context.getRequestId());
        jobExecDetail.setGroupName(jobDesc.getGroupName());
        jobExecDetail.setJobName(jobDesc.getJobName());
        jobExecDetail.setSchedulerEndpoint(StringUtils.splitToList(context.getSchedulerName(), StringUtils.UNDER_LINE_SEPARATOR).get(1));
        jobExecDetail.setScheduleStart(DateUtil.date(new Date()));
        jobExecDetail.setExecuteStatus(JobExecuteStatus.PENDING);
        return jobExecDetail;
    }

    private JobExecDetail buildSchedulerEndJobExecDetail(HodorJobExecutionContext context, Host host) {
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(context.getRequestId());
        jobExecDetail.setGroupName(context.getJobKey().getGroupName());
        jobExecDetail.setJobName(context.getJobKey().getJobName());
        jobExecDetail.setScheduleEnd(DateUtil.date(new Date()));
        jobExecDetail.setActuatorEndpoint(host.getEndpoint());
        return jobExecDetail;
    }

    private JobExecDetail buildFinishJobExecDetail(JobExecuteResponse response) {
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(response.getRequestId());
        jobExecDetail.setExecuteStatus(response.getStatus());
        if (!StringUtils.isBlank(response.getStartTime())) {
            jobExecDetail.setExecuteStart(DateUtil.parse(response.getStartTime(), DatePattern.NORM_DATETIME_FORMAT));
        }
        if (!StringUtils.isBlank(response.getCompleteTime())) {
            jobExecDetail.setExecuteEnd(DateUtil.parse(response.getCompleteTime(), DatePattern.NORM_DATETIME_FORMAT));
            jobExecDetail.setElapsedTime(response.getProcessTime());
        }
        jobExecDetail.setComments(response.getComments());
        return jobExecDetail;
    }

}
