package org.dromara.hodor.server.service;

import org.dromara.hodor.server.component.LifecycleComponent;
import org.dromara.hodor.server.config.HodorServerProperties;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.server.restservice.RestServiceRequestHandler;
import org.springframework.stereotype.Service;

/**
 * hodor remote service
 *
 * TODO: 后续再实现
 *
 * @author tomgs
 * @since 2020/6/28
 */
@Service
public class RestServerService implements LifecycleComponent {

    private final NetServer netServer;

    public RestServerService(final HodorServerProperties properties) {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, properties.getNetServerHost());
        attribute.put(RemotingConst.PORT_KEY, properties.getNetServerPort());
        attribute.put(RemotingConst.HTTP_PROTOCOL, true);

        RestServiceRequestHandler handler = new RestServiceRequestHandler();

        NetServerTransport netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        this.netServer = netServerTransport.bind(attribute, handler);
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
