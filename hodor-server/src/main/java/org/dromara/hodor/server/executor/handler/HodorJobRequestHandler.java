package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.core.enums.RequestType;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.executor.request.SchedulerRequestBody;
import org.dromara.hodor.server.service.RegisterService;
import org.dromara.hodor.server.service.RemotingClientService;

import java.util.List;

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

    private final HodorJobExecutionContext context;

    private final RemotingMessageSerializer serializer;

    public HodorJobRequestHandler(final HodorJobExecutionContext context) {
        ServiceProvider serviceProvider = ServiceProvider.getInstance();
        this.clientService = serviceProvider.getBean(RemotingClientService.class);
        this.registerService = serviceProvider.getBean(RegisterService.class);
        this.context = context;
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
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
        byte[] requestBody = serializer.serialize(SchedulerRequestBody.fromContext(context));
        return RemotingMessage.builder()
            .header(buildHeader(requestBody.length))
            .body(requestBody)
            .build();
    }

    private Header buildHeader(int bodyLength) {
        return Header.builder()
                .crcCode(RemotingConst.RPC_CRC_CODE)
                .version(RemotingConst.RPC_VERSION)
                .type(RequestType.JOB_EXEC_REQUEST.getCode())
                .length(bodyLength)
                .build();
    }

}
