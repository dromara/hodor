/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * Contributor license agreements.See the NOTICE file distributed with
 * This work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * he License.You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.dromara.hodor.remoting.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.message.ResponseBody;
import org.dromara.hodor.remoting.netty.rpc.codec.RpcRequestDecoder;
import org.dromara.hodor.remoting.netty.rpc.codec.RpcResponseEncoder;

/**
 * The type Netty server initializer.
 *
 * @author xiaoyu
 * @author tomgs
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyChannelHandler serverHandler;

    /**
     * Instantiates a new Netty server initializer.
     *
     * @param serverHandler the server handler
     */
    NettyServerInitializer(NettyChannelHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
        if (serverHandler.isHttpProtocol()) {
            channel.pipeline().addLast("http", new HttpServerCodec());
            channel.pipeline().addLast("websocket", new WebSocketServerCompressionHandler());
            channel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(1024 * 1024 * 64));
            channel.pipeline().addLast("chunkedWriter", new ChunkedWriteHandler());
        } else if (serverHandler.isTcpProtocol()) {
            //TODO: impl tcp
            channel.pipeline().addLast(new RpcRequestDecoder(RemotingConst.MAX_FRAME_LENGTH, RemotingConst.LENGTH_FIELD_OFFSET, RemotingConst.LENGTH_FIELD_LENGTH));
            channel.pipeline().addLast(new RpcResponseEncoder(ResponseBody.class));
        } else {
            throw new UnsupportedOperationException("unsupported protocol.");
        }
        channel.pipeline().addLast(serverHandler);
    }
}
