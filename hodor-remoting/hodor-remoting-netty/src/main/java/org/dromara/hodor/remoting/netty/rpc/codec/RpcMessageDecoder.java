package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

import java.util.List;

/**
 * rpc request decoder for rpc server
 *
 * @author tomgs
 * @since 2020/9/10
 */
public class RpcMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 20 is header message length
        if (in.readableBytes() < RemotingConst.LENGTH_OF_HEADER) {
            return;
        }
        in.markReaderIndex();
        Header header = CodecUtils.parseHeader(in);
        // parse body
        int bodyLength = header.getLength();
        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] requestBody = new byte[bodyLength];
        in.readBytes(requestBody);
        out.add(RemotingMessage.builder().header(header).body(requestBody).build());
    }

}
