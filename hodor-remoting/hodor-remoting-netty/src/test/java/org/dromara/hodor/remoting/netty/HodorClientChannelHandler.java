package org.dromara.hodor.remoting.netty;

import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * hodor client channel handler
 *
 * @author tomgs
 * @since 2020/9/7
 */
public class HodorClientChannelHandler implements HodorChannelHandler {

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
    public void received(HodorChannel channel, Object message) {

    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {

    }

    @Override
    public void timeout(HodorChannel channel) {

    }

}
