package org.dromara.hodor.server.executor.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.concurrent.FutureCallback;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.loadbalance.LoadBalance;
import org.dromara.hodor.common.loadbalance.LoadBalanceEnum;
import org.dromara.hodor.common.loadbalance.LoadBalanceFactory;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.core.Constants.FlowNodeConstants;
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
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.exception.IllegalJobExecuteStateException;
import org.dromara.hodor.server.executor.exception.JobScheduleException;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.manager.JobExecuteManager;

/**
 * job request executor
 *
 * @author tomgs
 * @since 2020/9/22
 */
@Slf4j
public class HodorJobRequestHandler implements RequestHandler {

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

    public void preHandle(final HodorJobExecutionContext context) {
        JobExecuteManager.getInstance().addSchedulerStartJob(context);
        // check job is running
        final JobKey jobKey = context.getJobKey();
        if (JobExecuteManager.getInstance().isRunning(jobKey)) {
            throw new IllegalJobExecuteStateException("The job {} is running", jobKey);
        }
        // check available hosts
        final List<Host> hosts = actuatorNodeManager.getAvailableHosts(jobKey);
        if (hosts.isEmpty()) {
            throw new JobScheduleException("The job [{}] has no available actuator nodes", jobKey);
        }

        // TODO: get load balance type from job
        LoadBalance loadBalance = LoadBalanceFactory.getLoadBalance(LoadBalanceEnum.RANDOM.name());
        Host selected = loadBalance.select(hosts);
        hosts.remove(selected);
        hosts.add(selected);

        context.resetHosts(hosts);
    }

    public void handle(final HodorJobExecutionContext context) {
        log.info("Job [key:{}, id:{}] dispatch begins, details: {}", context.getJobKey(), context.getRequestId(), context);

        Exception jobException = null;
        final RemotingMessage request = getRequestBody(context);
        final List<Host> hosts = context.getHosts();
        for (int i = hosts.size() - 1; i >= 0; i--) {
            Host host = hosts.get(i);
            try {
                clientService.sendBidiRequest(host, request, new FutureCallback<RemotingMessage>() {
                    @Override
                    public void onSuccess(RemotingMessage response) {
                        Header header = response.getHeader();
                        RemotingResponse<JobExecuteResponse> remotingResponse = serializer.deserialize(response.getBody(), typeReference.getType());
                        resultHandle(header.getAttachment(), remotingResponse);
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        log.error("HodorJobRequestHandler exceptionCaught : {}", cause.getMessage(), cause);
                    }
                });
                JobExecuteManager.getInstance().addSchedulerEndJob(context, host);
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

    @Override
    public void postHandle(final HodorJobExecutionContext context) {
        log.info("Job [key:{}, id:{}] dispatch success.", context.getJobKey(), context.getRequestId());
    }

    @Override
    public void resultHandle(Map<String, Object> attachment, final RemotingResponse<JobExecuteResponse> remotingResponse) {
        HodorJobResponseHandler.INSTANCE.fireJobResponseHandler(remotingResponse);
    }

    @Override
    public void exceptionCaught(final HodorJobExecutionContext context, final Throwable t) {
        log.error("Job [key:{}, id:{}] dispatch exception, msg: {}.", context.getJobKey(), context.getRequestId(), t.getMessage(), t);
        RemotingResponse<JobExecuteResponse> errorResponse = getErrorResponse(context, t);
        HodorJobResponseHandler.INSTANCE.fireJobResponseHandler(errorResponse);
    }

    public RemotingResponse<JobExecuteResponse> getErrorResponse(final HodorJobExecutionContext context, final Throwable t) {
        JobExecuteResponse jobExecuteResponse = new JobExecuteResponse();
        jobExecuteResponse.setRequestId(context.getRequestId());
        jobExecuteResponse.setJobKey(context.getJobKey());
        jobExecuteResponse.setCompleteTime(DateUtil.formatDateTime(new Date()));
        jobExecuteResponse.setStatus(JobExecuteStatus.ERROR);
        jobExecuteResponse.setComments(ThreadUtils.getStackTraceInfo(t));
        return RemotingResponse.failed("InnerError", jobExecuteResponse);
    }

    private RemotingMessage getRequestBody(final HodorJobExecutionContext context) {
        byte[] requestBody = serializer.serialize(buildRequestFromContext(context));
        return RemotingMessage.builder()
            .header(buildHeader(requestBody.length, context))
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
            .jobCommandType(jobDesc.getJobCommandType())
            .jobParameters(jobDesc.getJobParameters())
            .extensibleParameters(jobDesc.getExtensibleParameters())
            .timeout(jobDesc.getTimeout())
            .retryCount(jobDesc.getRetryCount())
            .version(jobDesc.getVersion())
            .build();
    }

    private Header buildHeader(int bodyLength, HodorJobExecutionContext context) {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("schedulerName", context.getSchedulerName());
        if (context.getRootJobKey() != null) {
            attachment.put(FlowNodeConstants.ROOT_JOB_KEY, context.getRootJobKey().getKeyName());
        }
        return Header.builder()
            .id(context.getRequestId())
            .version(RemotingConst.DEFAULT_VERSION)
            .type(MessageType.JOB_EXEC_REQUEST.getType())
            .attachment(attachment)
            .length(bodyLength)
            .build();
    }

}
