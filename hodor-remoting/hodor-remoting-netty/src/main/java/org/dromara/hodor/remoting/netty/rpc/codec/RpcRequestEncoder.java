package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.exception.RemotingException;
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

    private final Class<RpcRequest<? extends RequestBody>> genericClass;

    public RpcRequestEncoder(final Class<RpcRequest<? extends RequestBody>> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest request, ByteBuf out) throws Exception {
        if (!genericClass.isInstance(request)) {
            throw new RemotingException("Illegal class, " + request.getClass().getName());
        }

        Header header = request.getHeader();
        RequestBody body = request.getBody();

        // write header
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

        if (log.isDebugEnabled()) {
            log.debug("Encoding request data:{}.", body);
        }

        byte[] dataByte = SerializeUtils.serialize(body);
        out.writeInt(dataByte.length);
        out.writeBytes(dataByte);
    }

}
