package org.dromara.hodor.client.demo;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.LocalHost;
import org.dromara.hodor.remoting.api.*;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RequestBody;

import java.util.concurrent.CountDownLatch;

/**
 * @author tomgs
 * @since 2021/2/24
 */
@Slf4j
public class RemotingClientTest {

    private final static CountDownLatch downLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, LocalHost.getIp());
        attribute.put(RemotingConst.PORT_KEY, 46367);
        attribute.put(RemotingConst.TCP_PROTOCOL, true);
        // handle request
        HodorChannelHandler handler = new HodorClientChannelHandler(downLatch);

        RemotingMessageSerializer serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();

        NetClientTransport clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
        NetClient client = clientTransport.connect(attribute, handler);
        HodorChannel connection = client.connection();

        System.out.println("channel is open:" + connection.isOpen());

        RequestBody body = JobExecuteRequest.builder()
            .requestId(123L)
            .jobPath("org.dromara.hodor.client.demo.job.JobList")
            .jobCommand("test2")
            .groupName("testGroup")
            .jobName("test2")
            .jobCommandType("java")
            .jobParameters("123")
            .build();
        byte[] requestBody = serializer.serialize(body);

        Header header = Header.builder()
                .crcCode(RemotingConst.MESSAGE_CRC_CODE)
                .type((byte)1)
                .version(RemotingConst.DEFAULT_VERSION)
                .length(requestBody.length)
                .build();

        RemotingMessage request = RemotingMessage.builder().header(header).body(requestBody).build();

        connection.send(request);

        downLatch.await();

        System.out.println("----------");
        connection.close();
        System.exit(0);
    }

}
