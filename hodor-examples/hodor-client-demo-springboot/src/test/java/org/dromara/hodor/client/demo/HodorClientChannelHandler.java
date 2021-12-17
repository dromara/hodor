package org.dromara.hodor.client.demo;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

import java.util.concurrent.CountDownLatch;

/**
 * hodor client channel handler
 *
 * @author tomgs
 * @since 2020/9/7
 */
@Slf4j
public class HodorClientChannelHandler implements HodorChannelHandler {

    private final CountDownLatch downLatch;

    public HodorClientChannelHandler(final CountDownLatch downLatch) {
        this.downLatch = downLatch;
    }

    @Override
    public void connected(HodorChannel channel) {
        log.info("{} channel connected.", channel);
    }

    @Override
    public void disconnected(HodorChannel channel) {
        log.info("{} channel disconnected.", channel);
        downLatch.countDown();
    }

    @Override
    public void send(HodorChannel channel, Object message) {
        log.info("{} channel send.", channel);
    }

    @Override
    public void received(HodorChannel channel, Object message) {
        log.info("{} channel received.", channel);
        System.out.println("=====>" + message);
        downLatch.countDown();
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.info("{} channel exceptionCaught.", channel);
        cause.printStackTrace();
        channel.close();
        downLatch.countDown();
    }

    @Override
    public void timeout(HodorChannel channel) {
        log.info("{} channel timeout.", channel);
        channel.close();
        downLatch.countDown();
    }

}
