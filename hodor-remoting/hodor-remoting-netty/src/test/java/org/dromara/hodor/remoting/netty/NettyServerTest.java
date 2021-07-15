package org.dromara.hodor.remoting.netty;

import java.io.IOException;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.junit.Test;

/**
 *  netty server test
 *
 * @author tomgs
 * @version 2020/9/6 1.0 
 */
public class NettyServerTest {

    @Test
    public void testNettyServer() throws Exception {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, "127.0.0.1");
        attribute.put(RemotingConst.PORT_KEY, 8080);
        attribute.put(RemotingConst.TCP_PROTOCOL, true);

        // handle request
        HodorServerChannelHandler handler = new HodorServerChannelHandler();

        NetServerTransport netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        NetServer netServer = netServerTransport.build(attribute, handler);
        netServer.bind();
    }
    
}
