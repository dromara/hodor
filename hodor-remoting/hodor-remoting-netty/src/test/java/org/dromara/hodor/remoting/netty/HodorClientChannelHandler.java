package org.dromara.hodor.remoting.netty;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * hodor client channel handler
 *
 * @author tomgs
 * @since 2020/9/7
 */
@Slf4j
public class HodorClientChannelHandler implements HodorChannelHandler {

    @Override
    public void connected(HodorChannel channel) {
        log.info("{} channel connected.", channel);
        channel.send("abc");
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
    }

    @Override
    public void timeout(HodorChannel channel) {
        log.info("{} channel timeout.", channel);
    }

}
