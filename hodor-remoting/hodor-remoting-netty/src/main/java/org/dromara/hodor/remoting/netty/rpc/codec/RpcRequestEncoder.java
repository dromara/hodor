package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.netty.rpc.Header;
import org.dromara.hodor.remoting.netty.rpc.RequestBody;
import org.dromara.hodor.remoting.netty.rpc.RpcRequest;

/**
 * rpc request encoder for rpc client
 *
 * @author tomgs
 * @since 2020/9/10
 */
@Slf4j
public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest<? extends RequestBody>> {

    private final Class<? extends RequestBody> genericClass;

    public RpcRequestEncoder(final Class<? extends RequestBody> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest request, ByteBuf out) {
        if (!genericClass.isInstance(request.getBody())) {
            log.error("Illegal class {}.", request.getClass().getName());
            return;
        }

        Header header = request.getHeader();
        RequestBody body = request.getBody();

        // write header
        CodecUtils.writeHeader(out, header);

        // write body
        if (body == null) {
            // write length
            out.writeInt(0);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Encoding request data:{}.", body);
        }

        byte[] dataByte = SerializeUtils.serialize(body);
        out.writeInt(dataByte.length);
        out.writeBytes(dataByte);
    }

}
