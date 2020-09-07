/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.dromara.hodor.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.utils.OSInfo;
import org.dromara.hodor.remoting.api.AbstractNetServer;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * NettyServer starter.
 *
 * @author xiaoyu
 */
public class NettyServer extends AbstractNetServer {

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private HodorChannel channel;

    private NettyChannelHandler serverHandler;

    private Class<? extends ServerSocketChannel> serverSocketChannelClass;

    NettyServer(Attribute attribute, HodorChannelHandler handler) {
        super(attribute, handler);
        init();
    }

    @Override
    public void bind() {
        this.bootstrap.group(bossGroup, workerGroup)
                .channel(serverSocketChannelClass)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024 * 1024, 16 * 1024 * 1024))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new NettyServerInitializer(serverHandler));
        ChannelFuture future = bootstrap.bind(getHost(), getPort());
        io.netty.channel.Channel channel = future.syncUninterruptibly().channel();
        this.channel = new NettyChannel(channel);
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (bootstrap != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void init() {
        this.serverHandler = new NettyChannelHandler(getAttribute(), this);
        this.bootstrap = new ServerBootstrap();
        if (useEpoll()) {
            this.bossGroup = new EpollEventLoopGroup(1, HodorThreadFactory.create("netty-epoll-ServerBoss", false));
            this.workerGroup = new EpollEventLoopGroup(getIoThreads(), HodorThreadFactory.create("netty-epoll-ServerWork", false));
            serverSocketChannelClass = EpollServerSocketChannel.class;
        } else {
            this.bossGroup = new NioEventLoopGroup(1, HodorThreadFactory.create("netty-nio-ServerBoss", false));
            this.workerGroup = new NioEventLoopGroup(getIoThreads(), HodorThreadFactory.create("netty-nio-ServerWork", false));
            serverSocketChannelClass = NioServerSocketChannel.class;
        }
    }

    private boolean useEpoll() {
        return Epoll.isAvailable() && OSInfo.isLinux() && getUseEpollNative();
    }

}
