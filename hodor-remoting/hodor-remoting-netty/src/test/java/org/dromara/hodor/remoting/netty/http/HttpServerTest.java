package org.dromara.hodor.remoting.netty.http;

import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetServer;
import org.dromara.hodor.remoting.api.NetServerTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.junit.After;
import org.junit.Test;

/**
 * http server test
 *
 * @author tomgs
 */
public class HttpServerTest {

    private NetServer netServer;

    @Test
    public void testHttpServer() throws Exception {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, "localhost");
        attribute.put(RemotingConst.PORT_KEY, 8888);
        attribute.put(RemotingConst.HTTP_PROTOCOL, true);

        HttpServerHandler handler = new HttpServerHandler();

        NetServerTransport netServerTransport = ExtensionLoader.getExtensionLoader(NetServerTransport.class).getDefaultJoin();
        this.netServer = netServerTransport.build(attribute, handler);
        this.netServer.bind();
    }

    @After
    public void close() {
        //this.netServer.close();
    }

}
