package org.dromara.hodor.client.core;

import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 *  request context
 *
 * @author tomgs
 * @version 2021/2/26 1.0 
 */
public class RequestContext {

    private final HodorChannel channel;

    private final RemotingMessage remotingMessage;

    public RequestContext(HodorChannel channel, RemotingMessage remotingMessage) {
        this.channel = channel;
        this.remotingMessage = remotingMessage;
    }

    public HodorChannel channel() {
        return channel;
    }

    public MessageType messageType() {
        return MessageType.to(remotingMessage.getHeader().getType());
    }

    public Header requestHeader() {
        return remotingMessage.getHeader();
    }

    public byte[] rawRequestBody() {
        return remotingMessage.getBody();
    }

}
