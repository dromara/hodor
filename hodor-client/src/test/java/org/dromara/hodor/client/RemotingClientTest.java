package org.dromara.hodor.client;

import java.util.concurrent.CountDownLatch;
import org.dromara.hodor.client.core.SchedulerRequestBody;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.LocalHost;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.NetClient;
import org.dromara.hodor.remoting.api.NetClientTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingRequest;
import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 * @author tomgs
 * @since 2021/2/24
 */
public class RemotingClientTest {

    private final static CountDownLatch downLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, LocalHost.getIp());
        attribute.put(RemotingConst.PORT_KEY, 46367);
        attribute.put(RemotingConst.TCP_PROTOCOL, true);
        // handle request
        HodorChannelHandler handler = new HodorClientChannelHandler(downLatch);

        NetClientTransport clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
        NetClient client = clientTransport.connect(attribute, handler);
        HodorChannel connection = client.connection();

        System.out.println("channel is open:" + connection.isOpen());

        Header header = Header.builder()
            .crcCode(RemotingConst.RPC_CRC_CODE)
            .type((byte)1)
            .version(RemotingConst.RPC_VERSION)
            .build();
        RequestBody body = SchedulerRequestBody.builder()
            .requestId(123L)
            .groupName("testGroup")
            .jobName("test1")
            .jobCommandType("java")
            .jobParameters("123")
            .build();
        RemotingRequest request = RemotingRequest.builder().header(header).body(body).build();

        connection.send(request);

        downLatch.await();
    }

}
