package org.dromara.hodor.remoting.api.message;

import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;

/**
 *  request context
 *
 * @author tomgs
 * @version 2021/2/26 1.0 
 */
public class RequestContext {

    private final HodorChannel channel;

    private final RemotingMessage remotingMessage;

    private final RemotingMessageSerializer serializer;

    private final MessageType messageType;

    private final Class<? extends RequestBody> requestType;

    public RequestContext(HodorChannel channel, RemotingMessage remotingMessage, final RemotingMessageSerializer serializer) {
        this.channel = channel;
        this.remotingMessage = remotingMessage;
        this.serializer = serializer;
        this.messageType = MessageType.to(remotingMessage.getHeader().getType());
        this.requestType = messageType.getMessageClass();
    }

    public HodorChannel channel() {
        return channel;
    }

    public MessageType messageType() {
        return messageType;
    }

    public Header requestHeader() {
        return remotingMessage.getHeader();
    }

    public byte[] rawRequestBody() {
        return remotingMessage.getBody();
    }

    public Class<? extends RequestBody> getRequestType() {
        return requestType;
    }

    public RemotingMessageSerializer serializer() {
        return serializer;
    }

}
