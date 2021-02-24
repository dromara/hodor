package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.ResponseBody;
import org.dromara.hodor.remoting.api.message.RemotingResponse;

/**
 * rpc response decoder for rpc client
 *
 * @author tomgs
 * @since 2020/9/10
 */
@Slf4j
public class RpcResponseDecoder extends LengthFieldBasedFrameDecoder {

    public RpcResponseDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        in = (ByteBuf) super.decode(ctx, in);
        if (in == null) {
            log.warn("Client receive response ByteBuf is null.");
            return null;
        }

        Header header = CodecUtils.parseHeader(in);
        if (header == null) {
            log.warn("Client receive response Header is null.");
            return null;
        }
        // decode body
        ByteBuf buf = in.readBytes(header.getLength());
        try {
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            ResponseBody body = SerializeUtils.deserialize(req, ResponseBody.class);
            return RemotingResponse.builder().body(body).header(header).build();
        } catch (Exception e) {
            throw new RemotingException(e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

}
