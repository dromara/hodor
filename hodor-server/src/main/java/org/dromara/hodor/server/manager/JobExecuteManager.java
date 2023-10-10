package org.dromara.hodor.server.manager;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.core.recoder.JobExecuteRecorder;
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
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteStatusResponse;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;

/**
 * job status manager
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class JobExecuteManager {

    private static volatile JobExecuteManager INSTANCE;

    private final JobExecuteRecorder jobExecuteRecorder;

    private final RemotingMessageSerializer serializer;

    private final TypeReference<RemotingResponse<JobExecuteStatusResponse>> typeReference;

    private JobExecuteManager() {
        this.jobExecuteRecorder = ServiceProvider.getInstance().getBean(JobExecuteRecorder.class);
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        this.typeReference = new TypeReference<RemotingResponse<JobExecuteStatusResponse>>() {
        };
    }

    public static JobExecuteManager getInstance() {
        if (INSTANCE == null) {
            synchronized (JobExecuteManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JobExecuteManager();
                }
            }
        }
        return INSTANCE;
    }

    public boolean isRunning(JobKey jobKey) {
        JobExecDetail jobExecDetail = jobExecuteRecorder.getJobExecDetail(jobKey);
        if (jobExecDetail == null || (jobExecDetail.getExecuteStatus() != JobExecuteStatus.READY
            && jobExecDetail.getExecuteStatus() != JobExecuteStatus.PENDING
            && jobExecDetail.getExecuteStatus() != JobExecuteStatus.RUNNING)) {
            return false;
        }
        /*// 去执行端查询状态，并且更新状态
        Host host = Host.of(jobExecDetail.getActuatorEndpoint());
        JobExecuteStatusRequest statusRequest = new JobExecuteStatusRequest();
        statusRequest.setRequestId(jobExecDetail.getRequestId());
        JobExecuteStatusResponse statusResponse = queryExecuteJobStatus(host, statusRequest);
        // 未查询到说明当前执行器上面并没有此任务运行
        if (statusResponse == null) {
            return false;
        }
        if (JobExecuteStatus.isFinished(statusResponse.getStatus())) {
            removeRunningJob(jobKey);
        }
        return JobExecuteStatus.isRunning(statusResponse.getStatus());*/
        return true;
    }

    public void removeRunningJob(JobKey jobKey) {
        jobExecuteRecorder.removeJobExecDetail(jobKey);
    }

    public void addSchedulerStartJob(HodorJobExecutionContext context) {
        JobExecDetail jobExecDetail = buildSchedulerStartJobExecDetail(context);
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_INSERT, jobExecDetail);
        jobExecuteRecorder.addJobExecDetail(jobExecDetail);
    }

    public void addSchedulerEndJob(HodorJobExecutionContext context, Host host) {
        JobExecDetail jobExecDetail = buildSchedulerEndJobExecDetail(context, host);
        jobExecuteRecorder.addJobExecDetail(jobExecDetail);
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_UPDATE, jobExecDetail);
    }

    public void addFinishJob(JobExecuteResponse jobExecuteResponse) {
        JobExecDetail jobExecDetail = buildFinishJobExecDetail(jobExecuteResponse);
        if (JobExecuteStatus.isFinished(jobExecDetail.getExecuteStatus())) {
            removeRunningJob(jobExecuteResponse.getJobKey());
        }
        jobExecuteRecorder.recordJobExecDetail(JobExecuteRecorder.OP_UPDATE, jobExecDetail);
    }

    public JobExecuteStatusResponse queryExecuteJobStatus(Host host, JobExecuteStatusRequest request) {
        return executeRequest(host, request, MessageType.FETCH_JOB_STATUS_REQUEST);
    }

    private JobExecDetail buildSchedulerStartJobExecDetail(HodorJobExecutionContext context) {
        JobDesc jobDesc = context.getJobDesc();
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(context.getInstanceId());
        jobExecDetail.setRequestId(context.getRequestId());
        jobExecDetail.setGroupName(jobDesc.getGroupName());
        jobExecDetail.setJobName(jobDesc.getJobName());
        jobExecDetail.setSchedulerEndpoint(StringUtils.splitToList(context.getSchedulerName(), StringUtils.UNDER_LINE_SEPARATOR).get(1));
        jobExecDetail.setScheduleStart(DateUtil.date());
        jobExecDetail.setExecuteStatus(JobExecuteStatus.READY);
        jobExecDetail.setShardingCount(context.getShardingCount());
        jobExecDetail.setShardingId(context.getShardingId());
        jobExecDetail.setShardingParams(context.getShardingParams());
        return jobExecDetail;
    }

    private JobExecDetail buildSchedulerEndJobExecDetail(HodorJobExecutionContext context, Host host) {
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(context.getInstanceId());
        jobExecDetail.setRequestId(context.getRequestId());
        jobExecDetail.setGroupName(context.getJobKey().getGroupName());
        jobExecDetail.setJobName(context.getJobKey().getJobName());
        jobExecDetail.setScheduleEnd(DateUtil.date());
        jobExecDetail.setActuatorEndpoint(host.getEndpoint());
        jobExecDetail.setExecuteStatus(JobExecuteStatus.PENDING);
        return jobExecDetail;
    }

    private JobExecDetail buildFinishJobExecDetail(JobExecuteResponse response) {
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setRequestId(response.getRequestId());
        jobExecDetail.setExecuteStatus(response.getStatus());
        if (!StringUtils.isBlank(response.getStartTime())) {
            jobExecDetail.setExecuteStart(DateUtil.parse(response.getStartTime(), DatePattern.NORM_DATETIME_FORMAT));
        }
        if (!StringUtils.isBlank(response.getCompleteTime())) {
            jobExecDetail.setExecuteEnd(DateUtil.parse(response.getCompleteTime(), DatePattern.NORM_DATETIME_FORMAT));
            jobExecDetail.setElapsedTime(response.getProcessTime());
            if (response.getResult() != null) {
                jobExecDetail.setJobExecData(response.getResult());
            }
        }
        jobExecDetail.setComments(response.getComments());
        return jobExecDetail;
    }

    public JobExecDetail queryJobExecDetail(final JobKey jobKey) {
        return jobExecuteRecorder.getJobExecDetail(jobKey);
    }

    public KillRunningJobResponse killRunningJob(JobExecDetail jobExecDetail) {
        Host host = Host.of(jobExecDetail.getActuatorEndpoint());
        KillRunningJobRequest killRunningJobRequest = new KillRunningJobRequest();
        killRunningJobRequest.setRequestId(jobExecDetail.getId());
        return this.executeRequest(host, killRunningJobRequest, MessageType.KILL_JOB_REQUEST);
    }

    public <R> R executeRequest(Host host, RequestBody requestBody, MessageType messageType) {
        byte[] body = serializer.serialize(requestBody);
        Header header = Header.builder()
            .id(requestBody.getRequestId())
            .type(messageType.getType())
            .length(body.length)
            .version(RemotingConst.DEFAULT_VERSION)
            .build();
        RemotingMessage remotingRequest = RemotingMessage.builder()
            .header(header)
            .body(body)
            .build();
        try {
            RemotingMessage remotingMessage = RemotingClient.getInstance().sendSyncRequest(host, remotingRequest, 1500);
            RemotingResponse<R> remotingResponse = serializer.deserialize(remotingMessage.getBody(), typeReference.getType());
            if (!remotingResponse.isSuccess()) {
                log.error("request failure, code: {}, msg: {}", remotingResponse.getCode(), remotingResponse.getMsg());
            }
            return remotingResponse.getData();
        } catch (Exception e) {
            log.error("execute request error, messageType: {}, errorMsg: {}", messageType, e.getMessage(), e);
        }
        return null;
    }

}
