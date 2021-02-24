package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * rpc request decoder for rpc server
 *
 * @author tomgs
 * @since 2020/9/10
 */
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        Header header = CodecUtils.parseHeader(in);
        // parse body
        ByteBuf buf = in.readBytes(header.getLength());
        try {
            byte[] requestBody = new byte[buf.readableBytes()];
            buf.readBytes(requestBody);

            // 反序列化放到业务线程当中去
            //Object requestBody = SerializeUtils.deserialize(req, Object.class);

            return RemotingMessage.builder().header(header).body(requestBody).build();
        } catch (Exception e) {
            throw new RemotingException("Message decoder exception: " + e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

}
