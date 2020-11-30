package org.dromara.hodor.server.remoting;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * abstract hodor client channel handler
 *
 * @author tomgs
 * @since 2020/11/30
 */
@Slf4j
public abstract class AbstractHodorClientChannelHandler<I> implements HodorChannelHandler {

    private Class<? extends I> inboundMessageType;

    protected AbstractHodorClientChannelHandler(Class<? extends I> inboundMessageType) {
        this.inboundMessageType = inboundMessageType;
    }

    @Override
    public void connected(HodorChannel channel) {

    }

    @Override
    public void disconnected(HodorChannel channel) {

    }

    @Override
    public void send(HodorChannel channel, Object message) {

    }

    @Override
    public void received(HodorChannel channel, Object message) throws Exception {
        if (!inboundMessageType.isInstance(message)) {
            return;
        }
        @SuppressWarnings("unchecked")
        I msg = (I) message;
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

    protected abstract void received0(HodorChannel channel, I message) throws Exception;
}
