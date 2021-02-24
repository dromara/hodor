package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * rpc request encoder for rpc client
 *
 * @author tomgs
 * @since 2020/9/10
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RemotingMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingMessage request, ByteBuf out) {
        Header header = request.getHeader();
        byte[] body = request.getBody();

        // write body
        if (body == null) {
            // write header
            header.setLength(0);
        } else if (body.length != header.getLength()) {
            String msg = String.format("Illegal message body, header length %s, but body length %s.", header.getLength(), body.length);
            throw new RemotingException(msg);
        }
        CodecUtils.writeHeader(out, header);
        // 放到业务线程当中去
        //byte[] dataByte = SerializeUtils.serialize(body);
        CodecUtils.writeBody(out, body);
    }

}
