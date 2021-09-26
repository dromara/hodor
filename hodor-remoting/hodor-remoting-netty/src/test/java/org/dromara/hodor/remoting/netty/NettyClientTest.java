package org.dromara.hodor.remoting.netty;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.NetClient;
import org.dromara.hodor.remoting.api.NetClientTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.junit.Test;

/**
 *  netty client test
 *
 * @author tomgs
 * @version 2020/9/6 1.0 
 */
public class NettyClientTest {

    @Test
    public void testNettyClient() throws InterruptedException, ConnectException {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, "127.0.0.1");
        attribute.put(RemotingConst.PORT_KEY, 8080);
        attribute.put(RemotingConst.TCP_PROTOCOL, true);
        // handle request
        HodorChannelHandler handler = new HodorClientChannelHandler();

        NetClientTransport clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
        NetClient client = clientTransport.build(attribute, handler);
        HodorChannel connection = client.connect();

        for (;;) {
            System.out.println("channel is open:" + connection.isOpen());
            connection.send(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
            //connection.send("Netty rocks!");
            TimeUnit.SECONDS.sleep(5);
        }
    }

}
