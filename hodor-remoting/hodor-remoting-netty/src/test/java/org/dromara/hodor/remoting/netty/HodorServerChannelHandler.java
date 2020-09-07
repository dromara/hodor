package org.dromara.hodor.remoting.netty;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 *  hodor server channel handler
 *
 * @author tomgs
 * @version 2020/9/6 1.0 
 */
@Slf4j
public class HodorServerChannelHandler implements HodorChannelHandler {

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
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.info("{} channel exceptionCaught.", channel);
    }

    @Override
    public void timeout(HodorChannel channel) {
        log.info("{} channel timeout.", channel);
    }

}
