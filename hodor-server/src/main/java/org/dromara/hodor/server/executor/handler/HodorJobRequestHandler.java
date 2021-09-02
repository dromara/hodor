package org.dromara.hodor.server.executor.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.concurrent.FutureCallback;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.remoting.api.RemotingClient;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dromara.hodor.server.executor.exception.IllegalJobExecuteStateException;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.manager.JobExecuteStatusManager;

/**
 * job request executor
 *
 * @author tomgs
 * @since 2020/9/22
 */
@Slf4j
public class HodorJobRequestHandler {

    private final RemotingClient clientService;

    private final ActuatorNodeManager actuatorNodeManager;

    private final RemotingMessageSerializer serializer;

    private final TypeReference<RemotingResponse<JobExecuteResponse>> typeReference;

    public HodorJobRequestHandler() {
        this.clientService = RemotingClient.getInstance();
        this.actuatorNodeManager = ActuatorNodeManager.getInstance();
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        this.typeReference = new TypeReference<RemotingResponse<JobExecuteResponse>>() {};
    }

    public void preHandle(HodorJobExecutionContext context) {
        // check job is running
        if (JobExecuteStatusManager.getInstance().isRunning(context.getJobKey())) {
            throw new IllegalJobExecuteStateException("job {} is running.", context.getJobKey());
        }
        JobExecuteStatusManager.getInstance().addSchedulerStartJob(context);
    }

    public void handle(final HodorJobExecutionContext context) {
        log.info("hodor job request handler, info {}.", context);
        RemotingMessage request = getRequestBody(context);
        List<Host> hosts = actuatorNodeManager.getAvailableHosts(context.getJobDesc().getGroupName());
        Exception jobException = null;
        for (int i = hosts.size() - 1; i >= 0; i--) {
            Host host = hosts.get(i);
            try {
                clientService.sendDuplexRequest(host, request, new FutureCallback<RemotingMessage>() {
                    @Override
                    public void onSuccess(RemotingMessage response) {
                        RemotingResponse<JobExecuteResponse> remotingResponse = serializer.deserialize(response.getBody(), typeReference.getType());
                        HodorJobResponseHandler.INSTANCE.fireJobResponseHandler(remotingResponse);
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        log.error("HodorJobRequestHandler exceptionCaught : {}", cause.getMessage(), cause);
                    }
                });
                JobExecuteStatusManager.getInstance().addSchedulerEndJob(context, host);
                jobException = null;
                break;
            } catch (Exception e) {
                jobException = e;
            }
        }
        if (jobException != null) {
            exceptionCaught(context, jobException);
        }
    }

    public void exceptionCaught(final HodorJobExecutionContext context, final Throwable t) {
        log.error("job {} request [id:{}] execute exception, msg: {}.", context.getRequestId(), context.getJobKey(), t.getMessage(), t);
        JobExecuteResponse jobExecuteResponse = new JobExecuteResponse();
        jobExecuteResponse.setRequestId(context.getRequestId());
        jobExecuteResponse.setCompleteTime(DateUtil.formatDateTime(new Date()));
        jobExecuteResponse.setStatus(JobExecuteStatus.ERROR);
        jobExecuteResponse.setComments(ThreadUtils.getStackTraceInfo(t));
        HodorJobResponseHandler.INSTANCE.fireJobExecuteInnerErrorHandler(jobExecuteResponse);
    }

    private RemotingMessage getRequestBody(final HodorJobExecutionContext context) {
        byte[] requestBody = serializer.serialize(buildRequestFromContext(context));
        return RemotingMessage.builder()
            .header(buildHeader(requestBody.length, context.getRequestId(), context.getSchedulerName()))
            .body(requestBody)
            .build();
    }

    private JobExecuteRequest buildRequestFromContext(final HodorJobExecutionContext context) {
        Long requestId = context.getRequestId();
        JobDesc jobDesc = context.getJobDesc();
        return JobExecuteRequest.builder()
            .requestId(requestId)
            .jobName(jobDesc.getJobName())
            .groupName(jobDesc.getGroupName())
            .jobPath(jobDesc.getJobPath())
            .jobCommand(jobDesc.getJobCommand())
            .jobCommandType(jobDesc.getJobCommandType().getName())
            .jobParameters(jobDesc.getJobParameters())
            .extensibleParameters(jobDesc.getExtensibleParameters())
            .timeout(jobDesc.getTimeout())
            .retryCount(jobDesc.getRetryCount())
            .build();
    }

    private Header buildHeader(int bodyLength, long requestId, String schedulerName) {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("schedulerName", schedulerName);
        return Header.builder()
            .id(requestId)
            .version(RemotingConst.DEFAULT_VERSION)
            .type(MessageType.JOB_EXEC_REQUEST.getCode())
            .attachment(attachment)
            .length(bodyLength)
            .build();
    }

}
