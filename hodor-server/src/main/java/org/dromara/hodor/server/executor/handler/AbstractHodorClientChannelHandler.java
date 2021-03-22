package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * abstract hodor client channel handler
 *
 * @author tomgs
 * @since 2020/11/30
 */
@Slf4j
public abstract class AbstractHodorClientChannelHandler implements HodorChannelHandler {

    @Override
    public void received(HodorChannel channel, Object message) throws Exception {
        if (!(message instanceof RemotingMessage)) {
            return;
        }
        RemotingMessage msg = (RemotingMessage) message;
        received0(channel, msg);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.error("hodor client channel [{}] exception caught, msg: {}", channel.getId(), cause.getMessage(), cause);
        channel.close();
    }

    @Override
    public void timeout(HodorChannel channel) {
        log.warn("channel [{}] is timeout.", channel.getId());
        channel.close();
    }

    protected abstract void received0(HodorChannel channel, RemotingMessage message) throws Exception;
}
