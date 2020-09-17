package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
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

    private final Class<? extends ResponseBody> genericClass;

    public RpcResponseEncoder(final Class<? extends ResponseBody> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse response, ByteBuf out) {
        if (!genericClass.isInstance(response)) {
            log.error("Illegal class {}.", response.getClass().getName());
            return;
        }
        Header header = response.getHeader();
        ResponseBody body = response.getBody();

        CodecUtils.writeHeader(out, header);

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
