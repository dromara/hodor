package org.dromara.hodor.server.executor.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.core.JobDesc;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.service.RegisterService;
import org.dromara.hodor.server.service.RemotingClientService;

/**
 * job request executor
 *
 * @author tomgs
 * @since 2020/9/22
 */
@Slf4j
public class HodorJobRequestHandler extends HodorRunnable {

    private final RemotingClientService clientService;

    private final RegisterService registerService;

    private final RemotingMessageSerializer serializer;

    private final HodorJobExecutionContext context;

    public HodorJobRequestHandler(final HodorJobExecutionContext context) {
        final ServiceProvider serviceProvider = ServiceProvider.getInstance();
        this.clientService = serviceProvider.getBean(RemotingClientService.class);
        this.registerService = serviceProvider.getBean(RegisterService.class);
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        this.context = context;
    }

    @Override
    public void execute() {
        log.info("hodor job request handler, info {}.", context);
        RemotingMessage request = getRequestBody(context);
        List<Host> hosts = registerService.getAvailableHosts(context);
        for (int i = hosts.size() - 1; i >= 0; i--) {
            try {
                clientService.sendRequest(hosts.get(i), request);
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void exceptionCaught(Exception e) {
        log.error(e.getMessage(), e);
        //TODO: exception handler
    }

    private RemotingMessage getRequestBody(final HodorJobExecutionContext context) {
        byte[] requestBody = serializer.serialize(buildRequestFromContext(context));
        return RemotingMessage.builder()
            .header(buildHeader(requestBody.length, context.getSchedulerName()))
            .body(requestBody)
            .build();
    }

    private JobExecuteRequest buildRequestFromContext(HodorJobExecutionContext context) {
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

    private Header buildHeader(int bodyLength, String schedulerName) {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("schedulerName", schedulerName);
        return Header.builder()
            .crcCode(RemotingConst.MESSAGE_CRC_CODE)
            .version(RemotingConst.DEFAULT_VERSION)
            .type(MessageType.JOB_EXEC_REQUEST.getCode())
            .attachment(attachment)
            .length(bodyLength)
            .build();
    }

}
