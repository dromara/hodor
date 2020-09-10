package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.dromara.hodor.remoting.netty.rpc.RpcRequest;

/**
 * rpc request encoder for rpc client
 *
 * @author tomgs
 * @since 2020/9/10
 */
public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {

    }

}
