package org.dromara.hodor.remoting.netty;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannelFuture;
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
    public void testNettyClient() throws InterruptedException, ExecutionException {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, "127.0.0.1");
        attribute.put(RemotingConst.PORT_KEY, 8080);

        // handle request
        HodorChannelHandler handler = new HodorClientChannelHandler();

        NetClientTransport clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
        NetClient client = clientTransport.connect(attribute, handler);
        HodorChannelFuture future = client.connection();
        while (!future.isDone()) {
            TimeUnit.SECONDS.sleep(1);
        }
        future.get();

        System.out.println("------------");
    }

}
