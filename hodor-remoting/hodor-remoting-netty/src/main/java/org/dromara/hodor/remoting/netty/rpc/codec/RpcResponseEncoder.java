package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.netty.rpc.Header;
import org.dromara.hodor.remoting.netty.rpc.ResponseBody;
import org.dromara.hodor.remoting.netty.rpc.RpcResponse;

/**
 * rpc response encoder for rpc server
 *
 * @author tomgs
 * @since 2020/9/10
 */
@Slf4j
public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse<? extends ResponseBody>> {

    private final Class<RpcResponse<? extends ResponseBody>> genericClass;

    public RpcResponseEncoder(final Class<RpcResponse<? extends ResponseBody>> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse response, ByteBuf out) throws Exception {
        if (!genericClass.isInstance(response)) {
            log.error("消息类型 不匹配.");
            return;
        }
        Header header = response.getHeader();
        ResponseBody body = response.getBody();

        out.writeInt(header.getCrcCode());
        out.writeInt(header.getVersion());
        out.writeByte(header.getType());

        // write attachment
        if (MapUtils.isEmpty(header.getAttachment())) {
            out.writeInt(0);
        } else {
            byte[] attachmentByte = SerializeUtils.serialize(header.getAttachment());
            out.writeInt(attachmentByte.length);
            out.writeBytes(attachmentByte);
        }

        // write body
        if (body == null) {
            // write length
            out.writeInt(0);
            return;
        }

        byte[] dataByte = SerializeUtils.serialize(body);
        out.writeInt(dataByte.length);
        out.writeBytes(dataByte);
    }

}
