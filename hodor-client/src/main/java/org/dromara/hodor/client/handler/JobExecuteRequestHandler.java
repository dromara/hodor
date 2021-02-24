package org.dromara.hodor.client.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.core.SchedulerRequestBody;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

import java.util.concurrent.ExecutionException;

/**
 * job execute request handler
 *
 * @author tomgs
 * @since 2021/1/7
 */
@Slf4j
public class JobExecuteRequestHandler implements HodorChannelHandler {

    RemotingMessageSerializer serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();

    @Override
    public void send(HodorChannel channel, Object message) {
        log.info("send message {}", message);
    }

    @Override
    public void received(HodorChannel channel, Object message) throws Exception {
        log.info("received message {}", message);
        RemotingMessage request = (RemotingMessage) message;
        Header header = request.getHeader();
        byte[] body = request.getBody();
        SchedulerRequestBody requestBody = serializer.deserialize(body, SchedulerRequestBody.class);
        log.info("body message: {}.", requestBody);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.error("handler the request message has exception, message: {}.", cause.getMessage(), cause);
        try {
            channel.send(cause).get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("response error to client error.", e);
        }
        channel.close();
    }

}
