package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.remoting.api.message.RemotingRequest;

/**
 * rpc request decoder for rpc server
 *
 * @author tomgs
 * @since 2020/9/10
 */
@Slf4j
public class RpcRequestDecoder extends LengthFieldBasedFrameDecoder {

    public RpcRequestDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //in = (ByteBuf) super.decode(ctx, in);
        //if (in == null) {
        //    log.warn("Server receive request ByteBuf is null.");
        //    return null;
        //}

        Header header = CodecUtils.parseHeader(in);
        if (header == null) {
            log.warn("Server receive request Header is null.");
            return null;
        }
        //不是心跳包，则有消息内容体，解析消息内容
        ByteBuf buf = in.readBytes(header.getLength());
        try {
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            Object requestBody = SerializeUtils.deserialize(req, Object.class);
            return RemotingRequest.builder().header(header).body(requestBody).build();
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

}
