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
import org.dromara.hodor.remoting.netty.rpc.RequestBody;
import org.dromara.hodor.remoting.netty.rpc.RpcRequest;

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
    @SuppressWarnings("unchecked")
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        in = (ByteBuf) super.decode(ctx, in);
        if (in == null) {
            return null;
        }

        if (in.readableBytes() < 17) {
            log.error("Server receive client request, but readableBytes length less than header length!");
            ReferenceCountUtil.release(in);
            return null;
        }

        int crcCode = in.readInt();
        if (crcCode != RemotingConst.RPC_CRC_CODE) {
            throw new RemotingException("Server receive request crcCode is illegal.");
        }

        int version = in.readInt();
        if (version != RemotingConst.RPC_VERSION) {
            throw new RemotingException("Server receive request version is illegal.");
        }

        byte type = in.readByte();
        if (type == 0) {
            log.debug("Server receive heartbeat response from rpc client.");
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

        int length = in.readInt();
        //不是心跳包，则有消息内容体，解析消息内容
        ByteBuf buf = in.readBytes(length);
        try {
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            RequestBody requestBody = SerializeUtils.deserialize(req, RequestBody.class);
            Header header = Header.builder().crcCode(crcCode).type(type).version(version).length(length).attachmentSize(attachmentSize).attachment(attachment).build();
            return RpcRequest.builder().header(header).body(requestBody).build();
        } finally {
            ReferenceCountUtil.release(buf);
            ReferenceCountUtil.release(in);
        }
    }

}
