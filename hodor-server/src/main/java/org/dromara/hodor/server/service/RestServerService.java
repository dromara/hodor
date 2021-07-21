package org.dromara.hodor.server.service;

import lombok.extern.slf4j.Slf4j;
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
 * @author tomgs
 * @since 2020/6/28
 */
@Slf4j
@Service
public class RestServerService implements LifecycleComponent {

    private final NetServer netServer;

    private final HodorServerProperties properties;

    public RestServerService(final HodorServerProperties properties) {
        this.properties = properties;
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, properties.getNetServerHost());
        attribute.put(RemotingConst.PORT_KEY, properties.getNetServerPort());
        attribute.put(RemotingConst.HTTP_PROTOCOL, true);

        RestServiceRequestHandler handler = new RestServiceRequestHandler(properties);

        NetServerTransport netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        this.netServer = netServerTransport.build(attribute, handler);
    }

    @Override
    public void start() {
        log.info("RestServerService staring, endpoint:[{}:{}]", properties.getNetServerHost(), properties.getNetServerPort());
        Thread restServerServiceThread = new Thread(() -> {
            try {
                netServer.bind();
            } catch (Exception e) {
                log.error("RestServerService start exception, {}", e.getMessage(), e);
                System.exit(-1);
            }
        }, "hodor-scheduler-rest-server");
        restServerServiceThread.setDaemon(true);
        restServerServiceThread.start();

    }

    @Override
    public void stop() {
        netServer.close();
    }

}
