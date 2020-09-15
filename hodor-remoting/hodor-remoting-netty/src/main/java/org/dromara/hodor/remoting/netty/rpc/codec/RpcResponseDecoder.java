package org.dromara.hodor.remoting.netty.rpc.codec;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.netty.rpc.Header;
import org.dromara.hodor.remoting.netty.rpc.ResponseBody;
import org.dromara.hodor.remoting.netty.rpc.RpcResponse;

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
    @SuppressWarnings("unchecked")
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        in = (ByteBuf) super.decode(ctx, in);
        if (in == null) {
            throw new RemotingException("Client receive response ByteBuf is null.");
        }

        int headerLength = in.readableBytes();
        if (headerLength < 17) {
            throw new RemotingException("Client receive response ByteBuf length < header.length.");
        }

        int crcCode = in.readInt();
        if (crcCode != RemotingConst.RPC_CRC_CODE) {
            throw new RemotingException("Client receive response crcCode is illegal.");
        }

        int version = in.readInt();
        if (version != RemotingConst.RPC_VERSION) {
            throw new RemotingException("Client receive response version is illegal.");
        }

        byte type = in.readByte();
        if (type == 0) {
            log.debug("Client receive heartbeat response from RPC server.");
            ReferenceCountUtil.release(in);
            return null;
        }

        int attachmentSize = in.readInt();
        Map<String, Object> attachment = Maps.newHashMap();
        if (attachmentSize != 0) {
            ByteBuf buf = in.readBytes(attachmentSize);
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            attachment = (Map<String, Object>)SerializeUtils.deserialize(req, Map.class);
            ReferenceCountUtil.release(buf);
        }

        // decode body
        int length = in.readInt();
        ByteBuf buf = in.readBytes(length);
        try {
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            ResponseBody body = SerializeUtils.deserialize(req, ResponseBody.class);
            Header header = Header.builder().crcCode(crcCode).type(type).version(version).length(length).attachment(attachment).build();
            return RpcResponse.builder().body(body).header(header).build();
        } catch (Exception e) {
            throw new RemotingException(e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(buf);
            ReferenceCountUtil.release(in);
        }
    }

}
