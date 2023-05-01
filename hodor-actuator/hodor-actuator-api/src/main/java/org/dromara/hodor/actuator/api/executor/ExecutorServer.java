package org.dromara.hodor.actuator.api.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.actuator.api.handler.JobRequestHandler;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;

/**
 * 执行器服务
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class ExecutorServer {

    private final NetServer netServer;

    private final HodorProperties properties;

    public ExecutorServer(final RequestHandleManager requestHandleManager,
                          final RemotingMessageSerializer remotingMessageSerializer,
                          final HodorProperties properties) {
        this.properties = properties;
        final Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, this.properties.getHost());
        attribute.put(RemotingConst.PORT_KEY, this.properties.getPort());
        attribute.put(RemotingConst.TCP_PROTOCOL, true);

        final JobRequestHandler handler = new JobRequestHandler(requestHandleManager, remotingMessageSerializer);
        final NetServerTransport netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        this.netServer = netServerTransport.build(attribute, handler);
    }

    public void start() {
        log.info("ExecutorServer starting host:{}, port:{}", properties.getHost(), properties.getPort());
        try {
            netServer.bind();
        } catch (Exception e) {
            log.error("ExecutorServer start exception, {}", e.getMessage(), e);
            System.exit(-1);
        }
    }

    public void close() {
        netServer.close();
    }

}
