package org.dromara.hodor.remoting.netty.rpc.serialize;

import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.utils.ProtostuffUtils;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;

/**
 *  默认消息序列化器
 *
 * @author tomgs
 * @version 2021/2/24 1.0
 */
@Join
public class DefaultMessageSerializer implements RemotingMessageSerializer {

    @Override
    public byte[] serialize(Object requestBody) {
        return ProtostuffUtils.serialize(requestBody);
    }

    @Override
    public <T> T deserialize(byte[] byteData, Class<T> cls) {
        return ProtostuffUtils.deserialize(byteData, cls);
    }

}
