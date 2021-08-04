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
 *  remoting message encode and decode
 *
 * @author tomgs
 * @version 2021/7/28 1.0 
 */
public class RemotingMessageCodec extends ByteToMessageCodec<RemotingMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingMessage message, ByteBuf out) {
        Header header = message.getHeader();
        byte[] body = message.getBody();
        // write body
        if (body == null) {
            // write header
            header.setLength(0);
        } else if (body.length != header.getLength()) {
            String msg = String.format("Illegal message body, header length %s, but body length %s.", header.getLength(), body.length);
            throw new RemotingException(msg);
        }
        CodecUtils.writeHeader(out, header);
        CodecUtils.writeBody(out, body);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 20 is header message length
        if (in.readableBytes() < RemotingConst.LENGTH_OF_HEADER) {
            return;
        }
        in.markReaderIndex();
        Header header;
        try {
            header = CodecUtils.parseHeader(in);
        } catch (ResetReaderIndexException e) {
            return;
        }
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
