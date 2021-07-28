package org.dromara.hodor.remoting.netty.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

import java.util.List;

/**
 *  
 *
 * @author tomgs
 * @version 2021/7/28 1.0 
 */
public class RemotingMessageCodec extends ByteToMessageCodec<RemotingMessage> {

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

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 20 is header message length
        if (in.readableBytes() < RemotingConst.LENGTH_OF_HEADER) {
            return;
        }
        in.markReaderIndex();
        Header header = CodecUtils.parseHeader(in);
        // parse body
        int bodyLength = header.getLength();
        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] requestBody = new byte[bodyLength];
        in.readBytes(requestBody);
        out.add(RemotingMessage.builder().header(header).body(requestBody).build());
    }

}
