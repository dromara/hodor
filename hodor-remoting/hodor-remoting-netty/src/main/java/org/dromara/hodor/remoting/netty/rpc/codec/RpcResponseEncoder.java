package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.dromara.hodor.remoting.netty.rpc.RpcResponse;

/**
 * rpc response encoder for rpc server
 *
 * @author tomgs
 * @since 2020/9/10
 */
public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) throws Exception {

    }

}
