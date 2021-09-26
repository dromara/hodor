package org.dromara.hodor.client.json;

import java.nio.charset.StandardCharsets;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.junit.Test;

/**
 * @author tomgs
 * @since 2021/7/29
 */
public class RemotingMessageTest {

    @Test
    public void testSerialization() {
        RemotingMessage remotingMessage = RemotingMessage.builder()
            .header(Header.builder()
                .magic((byte) 123)
                .id(12321)
                .length(123)
                .type((byte) 1)
                .build())
            .body("123123".getBytes(StandardCharsets.UTF_8))
            .build();

        byte[] bytes = SerializeUtils.serialize(remotingMessage);

        RemotingMessage message = SerializeUtils.deserialize(bytes, RemotingMessage.class);
        System.out.println(remotingMessage.equals(message));

    }

}
