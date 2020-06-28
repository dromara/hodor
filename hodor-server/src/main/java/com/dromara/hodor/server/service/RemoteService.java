package com.dromara.hodor.server.service;

import com.dromara.hodor.server.component.LifecycleComponent;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;

/**
 * hodor remote service
 *
 * @author tomgs
 * @since 2020/6/28
 */
public class RemoteService implements LifecycleComponent {

    private final NetServerTransport netServerTransport;
    private final Attribute attribute;
    private final NetServer netServer;

    public RemoteService() {
        this.netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        this.attribute = new Attribute();
        this.netServer = netServerTransport.bind(attribute, null);
    }

    @Override
    public void start() {
        netServer.bind();
    }

    @Override
    public void stop() {
        //netServer.close();
    }

}
