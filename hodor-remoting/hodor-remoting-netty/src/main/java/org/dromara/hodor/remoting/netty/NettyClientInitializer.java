package org.dromara.hodor.remoting.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.dromara.hodor.remoting.netty.rpc.codec.RemotingMessageCodec;

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
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        if (channelHandler.isHttpProtocol()) {
            channel.pipeline().addLast("http", new HttpClientCodec());
            /*channel.pipeline().addLast("websocket", WebSocketClientCompressionHandler.INSTANCE);*/
            channel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(1024 * 1024 * 64));
            channel.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
        } else if (channelHandler.isTcpProtocol()) {
            channel.pipeline().addLast(new RemotingMessageCodec());
        } else {
            throw new UnsupportedOperationException("unsupported protocol.");
        }
        channel.pipeline().addLast(channelHandler);
    }

}
