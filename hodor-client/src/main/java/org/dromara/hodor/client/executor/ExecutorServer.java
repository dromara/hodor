package org.dromara.hodor.client.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.config.HodorProperties;
import org.dromara.hodor.client.handler.JobRequestHandler;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;
import org.dromara.hodor.remoting.api.RemotingConst;

/**
 * 执行器服务
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class ExecutorServer {

    private final NetServer netServer;

    private final HodorProperties properties = ServiceProvider.getInstance().getBean(HodorProperties.class);

    public ExecutorServer() {
        final Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, properties.getHost());
        attribute.put(RemotingConst.PORT_KEY, properties.getPort());
        attribute.put(RemotingConst.TCP_PROTOCOL, true);

        final JobRequestHandler handler = new JobRequestHandler();
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
