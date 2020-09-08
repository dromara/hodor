package org.dromara.hodor.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.dromara.hodor.common.utils.OSInfo;
import org.dromara.hodor.remoting.api.AbstractNetClient;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannelFuture;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 *  netty client
 *
 * @author tomgs
 * @version 2020/9/6 1.0 
 */
public class NettyClient extends AbstractNetClient {

    private final Bootstrap bootstrap;

    public NettyClient(final Attribute attribute, final HodorChannelHandler channelHandler) {
        super(attribute, channelHandler);
        this.bootstrap = new Bootstrap();
        init();
    }

    @Override
    @SneakyThrows
    public HodorChannelFuture connection() {
        ChannelFuture future = bootstrap.connect(getHost(), getPort());
        if (future.isSuccess()) {
            future.await(1000, TimeUnit.MILLISECONDS);
        }
        return new NettyChannelFuture(future);
    }

    private void init() {
        EventLoopGroup eventLoopGroup;
        Class<? extends SocketChannel> socketChannelClass;
        NettyChannelHandler channelHandler = new NettyChannelHandler(getAttribute(), this);

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
    }

    private boolean useEpoll() {
        return Epoll.isAvailable() && OSInfo.isLinux() && getUseEpollNative();
    }

}
