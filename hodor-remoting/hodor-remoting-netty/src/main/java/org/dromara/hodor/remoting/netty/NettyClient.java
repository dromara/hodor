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
import java.net.ConnectException;
import lombok.SneakyThrows;
import org.dromara.hodor.common.utils.OSInfo;
import org.dromara.hodor.remoting.api.AbstractNetClient;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.RemotingConst;

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
    public HodorChannel connection() {
        ChannelFuture future = bootstrap.connect(getHost(), getPort());
        future.get();
        if (!future.isSuccess()) {
            throw new ConnectException(String.format("connect %s:%s failure.", getHost(), getPort()));
        }
        return new NettyChannel(future.channel());
    }

    private void init() {
        EventLoopGroup eventLoopGroup;
        Class<? extends SocketChannel> socketChannelClass;
        NettyChannelHandler channelHandler = new NettyChannelHandler(getAttribute(), this);
        Integer connectTimeout = getAttribute().getProperty(RemotingConst.NET_TIMEOUT_KEY, 1000);

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
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator());
        bootstrap.handler(new NettyClientInitializer(channelHandler));
    }

    private boolean useEpoll() {
        return Epoll.isAvailable() && OSInfo.isLinux() && getUseEpollNative();
    }

}
