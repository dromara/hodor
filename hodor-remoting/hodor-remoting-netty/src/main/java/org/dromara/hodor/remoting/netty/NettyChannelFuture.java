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

import io.netty.channel.ChannelFuture;
import java.util.concurrent.ExecutionException;

import io.netty.channel.ChannelFutureListener;
import org.dromara.hodor.remoting.api.HodorChannelFutureListener;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelFuture;

/**
 * NettyChannelFuture
 *
 * @author xiaoyu
 */
public class NettyChannelFuture implements HodorChannelFuture {

    private final ChannelFuture future;

    public NettyChannelFuture(ChannelFuture future) {
        this.future = future;
    }

    @Override
    public HodorChannel channel() {
        return new NettyChannel(future.channel());
    }

    @Override
    public boolean isDone() {
        return this.future.isDone();
    }

    @Override
    public boolean isSuccess() {
        return this.future.isSuccess();
    }

    @Override
    public Throwable cause() {
        return this.future.cause();
    }

    @Override
    public Void get() throws ExecutionException, InterruptedException {
        return future.get();
    }

    @Override
    public void operationComplete(HodorChannelFutureListener<HodorChannelFuture> listener) {
        this.future.addListener((ChannelFutureListener) future ->
                listener.operationComplete(new NettyChannelFuture(future)));
    }
}
