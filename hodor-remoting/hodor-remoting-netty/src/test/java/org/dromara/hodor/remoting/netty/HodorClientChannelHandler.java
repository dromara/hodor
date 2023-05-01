package org.dromara.hodor.remoting.netty;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * hodor client channel handler
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorClientChannelHandler implements HodorChannelHandler {

    @Override
    public void connected(HodorChannel channel) {
        log.info("{} channel connected.", channel);
    }

    @Override
    public void disconnected(HodorChannel channel) {
        log.info("{} channel disconnected.", channel);
    }

    @Override
    public void send(HodorChannel channel, Object message) {
        log.info("{} channel send.", channel);
    }

    @Override
    public void received(HodorChannel channel, Object message) {
        log.info("{} channel received.", channel);
        System.out.println("=====>" + message);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.info("{} channel exceptionCaught.", channel);
        cause.printStackTrace();
        channel.close();
    }

    @Override
    public void timeout(HodorChannel channel) {
        log.info("{} channel timeout.", channel);
        channel.close();
    }

}
