package org.dromara.hodor.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import lombok.SneakyThrows;
import org.dromara.hodor.remoting.api.*;

import java.util.concurrent.TimeUnit;

/**
 *  netty client
 *
 * @author tomgs
 * @version 2020/9/6 1.0 
 */
public class NettyClient extends AbstractNetClient {

    private final Bootstrap bootstrap;
    private final Attribute attribute;
    private final NettyChannelHandler channelHandler;

    public NettyClient(Attribute attribute, HodorChannelHandler channelHandler) {
        super(attribute, channelHandler);
        this.attribute = attribute;
        this.bootstrap = new Bootstrap();
        this.channelHandler = new NettyChannelHandler(attribute, channelHandler);
    }

    @SneakyThrows
    @Override
    public void connection() {
        String host = attribute.getProperty(RemotingConst.HOST_KEY, "0.0.0.0");
        Integer port = attribute.getProperty(RemotingConst.PORT_KEY, 2870);

        //TODO: impl
        bootstrap.group();

        bootstrap.option(ChannelOption.TCP_NODELAY, true)
                // 如果是延时敏感型应用，建议关闭Nagle算法
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);

        bootstrap.handler(new NettyClientInitializer(channelHandler));

        ChannelFuture future = bootstrap.connect(host, port);
        if (future.isSuccess()) {
            future.await(1000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void connected(HodorChannel channel) {
        super.connected(channel);
    }

    @Override
    public void disconnected(HodorChannel channel) {
        super.disconnected(channel);
    }

    @Override
    public void send(HodorChannel channel, Object message) {
        super.send(channel, message);
    }

    @Override
    public void received(HodorChannel channel, Object message) {
        super.received(channel, message);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        super.exceptionCaught(channel, cause);
    }

    @Override
    public void timeout(HodorChannel channel) {
        super.timeout(channel);
    }

}
