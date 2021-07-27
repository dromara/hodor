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

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.dromara.hodor.remoting.netty.http.HttpMessageWrapper;

/**
 * NettyServerHandler.
 *
 * @author xiaoyu
 * @author tomgs
 */
@ChannelHandler.Sharable
public class NettyChannelHandler extends ChannelDuplexHandler {

    private final HodorChannelHandler channelHandler;

    private final Attribute attribute;

    public NettyChannelHandler(final Attribute attribute, final HodorChannelHandler channelHandler) {
        if (attribute == null) {
            throw new IllegalArgumentException("attribute is null");
        }
        if (channelHandler == null) {
            throw new IllegalArgumentException("handler is null");
        }
        this.channelHandler = channelHandler;
        this.attribute = attribute;
        //Timeout handling.
    }

    public boolean isHttpProtocol() {
        return attribute.getProperty(RemotingConst.HTTP_PROTOCOL, false);
    }

    public boolean isTcpProtocol() {
        return attribute.getProperty(RemotingConst.TCP_PROTOCOL, false);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        HodorChannel channel = new NettyChannel(ctx.channel());
        channelHandler.connected(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        HodorChannel channel = new NettyChannel(ctx.channel());
        channelHandler.disconnected(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HodorChannel channel = new NettyChannel(ctx.channel());
        if (isTcpProtocol()) {
            super.channelRead(ctx, msg);
            channelHandler.received(channel, msg);
            return;
        }
        if (isHttpProtocol() && msg instanceof FullHttpRequest) {
            HodorHttpRequest hodorHttpRequest = HttpMessageWrapper.requestWrapper((FullHttpRequest) msg);
            channelHandler.received(channel, hodorHttpRequest);
            return;
        }
        if (isHttpProtocol() && msg instanceof FullHttpResponse) {
            HodorHttpResponse hodorHttpResponse = HttpMessageWrapper.responseWrapper((FullHttpResponse) msg);
            channelHandler.received(channel, hodorHttpResponse);
            return;
        }
        throw new RemotingException("check the protocol set.");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        HodorChannel channel = new NettyChannel(ctx.channel());
        if (isTcpProtocol()) {
            super.write(ctx, msg, promise);
            channelHandler.send(channel, msg);
            return;
        }
        if (isHttpProtocol() && msg instanceof HodorHttpRequest) {
            FullHttpRequest rawHttpRequest = HttpMessageWrapper.requestRawWrapper((HodorHttpRequest) msg);
            ctx.writeAndFlush(rawHttpRequest);
            return;
        }
        if (isHttpProtocol() && msg instanceof HodorHttpResponse) {
            FullHttpResponse httpResponse = HttpMessageWrapper.responseRawWrapper((HodorHttpResponse) msg);
            ctx.write(httpResponse);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        throw new RemotingException("check the protocol set.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        HodorChannel channel = new NettyChannel(ctx.channel());
        channelHandler.exceptionCaught(channel, cause);
    }

}
