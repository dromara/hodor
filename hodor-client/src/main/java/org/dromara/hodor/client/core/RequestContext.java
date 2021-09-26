package org.dromara.hodor.client.core;

import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RequestBody;

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

    private Class<? extends RequestBody> requestType;

    public RequestContext(HodorChannel channel, RemotingMessage remotingMessage, final RemotingMessageSerializer serializer) {
        this.channel = channel;
        this.remotingMessage = remotingMessage;
        this.serializer = serializer;
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

    public void setRequestType(Class<? extends RequestBody> requestType) {
        this.requestType = requestType;
    }

    public Class<? extends RequestBody> getRequestType() {
        return requestType;
    }

    public RemotingMessageSerializer serializer() {
        return serializer;
    }

}
