package org.dromara.hodor.remoting.netty.rpc.codec;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.Header;

import java.util.Map;

/**
 * codec utils
 *
 * @author tomgs
 * @since 2020/9/17
 */
@Slf4j
public class CodecUtils {

    private static final RemotingMessageSerializer serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseAttachment(ByteBuf in, int attachmentSize) {
        Map<String, Object> attachment = Maps.newHashMap();
        if (attachmentSize != 0) {
            byte[] req = new byte[attachmentSize];
            in.readBytes(req);
            attachment = (Map<String, Object>) serializer.deserialize(req, Map.class);
        }
        return attachment;
    }

    public static void writeHeader(ByteBuf out, Header header) {
        if (header == null) {
            throw new RemotingException("message header must be not null.");
        }
        out.writeShort(RemotingConst.MAGIC);
        out.writeLong(header.getId());
        out.writeByte(header.getVersion());
        out.writeByte(header.getType());

        // write attachment
        if (MapUtils.isEmpty(header.getAttachment())) {
            out.writeInt(0);
        } else {
            byte[] attachmentByte = serializer.serialize(header.getAttachment());
            out.writeInt(attachmentByte.length);
            out.writeBytes(attachmentByte);
        }

        // write length
        out.writeInt(header.getLength());
    }

    public static Header parseHeader(ByteBuf in) {
        short magic = in.readShort();
        if (RemotingConst.MAGIC != magic) {
            throw new RemotingException("magic number is illegal, " + magic);
        }

        long id = in.readLong();
        byte version = in.readByte();
        if (version < RemotingConst.DEFAULT_VERSION) {
            throw new RemotingException("Server receive message version is illegal.");
        }

        byte type = in.readByte();
        if (type < 0) {
            throw new RemotingException("Server receive message type must >= 0.");
        }

        // attachment
        int attachmentSize = in.readInt();
        Map<String, Object> attachment = CodecUtils.parseAttachment(in, attachmentSize);

        // body length
        int length = in.readInt();
        if (length < 0) {
            throw new RemotingException("Server receive message length must >= 0.");
        }

        return Header.builder().magic(magic).id(id).type(type).version(version).length(length).attachment(attachment).build();
    }

    public static void writeBody(ByteBuf out, byte[] body) {
        if (body != null) {
            out.writeBytes(body);
        }
    }
}
