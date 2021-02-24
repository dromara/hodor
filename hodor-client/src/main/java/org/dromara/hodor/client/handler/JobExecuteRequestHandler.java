package org.dromara.hodor.client.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.core.SchedulerRequestBody;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * job execute request handler
 *
 * @author tomgs
 * @since 2021/1/7
 */
@Slf4j
public class JobExecuteRequestHandler implements HodorChannelHandler {

    @Override
    public void send(HodorChannel channel, Object message) {
        log.info("send message {}", message);
    }

    @Override
    public void received(HodorChannel channel, Object message) throws Exception {
        log.info("received message {}", message);
        SchedulerRequestBody requestBody = (SchedulerRequestBody) message;

    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.error("handler the request message has exception, message: {}.", cause.getMessage(), cause);
        channel.close();
    }

}
