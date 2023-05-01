package org.dromara.hodor.remoting.api.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * rpc message
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemotingMessage {

    private Header header;

    private byte[] body;

    @Override
    public String toString() {
        return "RemotingMessage {" +
                "header=" + header +
                ", body=" + (body == null ? "null" : new String(body, StandardCharsets.UTF_8)) +
                '}';
    }

}
