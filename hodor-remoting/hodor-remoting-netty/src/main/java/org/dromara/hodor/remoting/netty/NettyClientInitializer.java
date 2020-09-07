package org.dromara.hodor.remoting.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *  netty client initializer
 *
 * @author tomgs
 * @version 2020/9/7 1.0 
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyChannelHandler channelHandler;

    public NettyClientInitializer(final NettyChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("http", new HttpServerCodec());
        channel.pipeline().addLast("websocket", new WebSocketServerCompressionHandler());
        channel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(1024 * 1024 * 64));
        channel.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
        channel.pipeline().addLast(channelHandler);
    }

}
