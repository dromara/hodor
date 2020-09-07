package org.dromara.hodor.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import org.dromara.hodor.common.utils.OSInfo;
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

    private final NettyChannelHandler channelHandler;

    public NettyClient(Attribute attribute, HodorChannelHandler channelHandler) {
        super(attribute, channelHandler);
        this.bootstrap = new Bootstrap();
        this.channelHandler = new NettyChannelHandler(attribute, channelHandler);
    }

    @Override
    @SneakyThrows
    public void connection() {
        EventLoopGroup eventLoopGroup;
        Class<? extends SocketChannel> socketChannelClass;

        if (useEpoll()) {
            eventLoopGroup = new EpollEventLoopGroup();
            socketChannelClass = EpollSocketChannel.class;
        } else {
            eventLoopGroup = new NioEventLoopGroup();
            socketChannelClass = NioSocketChannel.class;
        }

        bootstrap.channel(socketChannelClass);
        bootstrap.group(eventLoopGroup);
        bootstrap.option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
        bootstrap.handler(new NettyClientInitializer(channelHandler));

        ChannelFuture future = bootstrap.connect(getHost(), getPort());
        if (future.isSuccess()) {
            future.await(1000, TimeUnit.MILLISECONDS);
        }
    }

    private boolean useEpoll() {
        return Epoll.isAvailable() && OSInfo.isLinux() && getUseEpollNative();
    }

}
