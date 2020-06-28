package com.dromara.hodor.server.service;

import com.dromara.hodor.server.component.LifecycleComponent;
import com.dromara.hodor.server.config.HodorServerProperties;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;
import org.dromara.hodor.remoting.api.RemotingConst;

/**
 * hodor remote service
 *
 * @author tomgs
 * @since 2020/6/28
 */
public class RemoteService implements LifecycleComponent {

    private final NetServer netServer;

    public RemoteService(final HodorServerProperties properties) {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, properties.getNetServerHost());
        attribute.put(RemotingConst.PORT_KEY, properties.getNetServerPort());

        NetServerTransport netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        this.netServer = netServerTransport.bind(attribute, null);
    }

    @Override
    public void start() {
        netServer.bind();
    }

    @Override
    public void stop() {
        netServer.close();
    }

}
