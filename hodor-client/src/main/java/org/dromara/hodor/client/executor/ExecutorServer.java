package org.dromara.hodor.client.executor;

import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.annotation.HodorProperties;
import org.dromara.hodor.client.handler.JobExecuteRequestHandler;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;
import org.dromara.hodor.remoting.api.RemotingConst;

/**
 * 执行器服务
 *
 * @author tomgs
 * @since 2021/1/6
 */
public class ExecutorServer {

    private final NetServer netServer;

    public ExecutorServer() {
        HodorProperties properties = ServiceProvider.getInstance().getBean(HodorProperties.class);

        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, properties.getHost());
        attribute.put(RemotingConst.PORT_KEY, properties.getPort());
        attribute.put(RemotingConst.TCP_PROTOCOL, true);

        JobExecuteRequestHandler handler = new JobExecuteRequestHandler();
        NetServerTransport netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        this.netServer = netServerTransport.bind(attribute, handler);
    }

    public void start() {
        netServer.bind();
    }

    public void close() {
        netServer.close();
    }

}
