package org.dromara.hodor.remoting.netty.rpc.codec;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.Header;

/**
 * @author tomgs
 * @since 2020/9/17
 */
@Slf4j
public class CodecUtils {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseAttachment(ByteBuf in, int attachmentSize) {
        Map<String, Object> attachment = Maps.newHashMap();
        if (attachmentSize != 0) {
            ByteBuf buf = in.readBytes(attachmentSize);
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            attachment = (Map<String, Object>) SerializeUtils.deserialize(req, Map.class);
            ReferenceCountUtil.release(buf);
        }
        return attachment;
    }

    public static void writeHeader(ByteBuf out, Header header) {
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
    }

    public static Header parseHeader(ByteBuf in) {
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

        // attachment
        int attachmentSize = in.readInt();
        Map<String, Object> attachment = CodecUtils.parseAttachment(in, attachmentSize);

        // body length
        int length = in.readInt();
        if (length <= 0) {
            return null;
        }

        return Header.builder().crcCode(crcCode).type(type).version(version).length(length).attachment(attachment).build();
    }

}
