package org.dromara.hodor.client.action;

import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.remoting.api.HodorChannelFuture;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * abstract action
 *
 * @author tomgs
 * @since 2021/2/26
 */
public abstract class AbstractAction extends HodorRunnable {

    private final RequestContext context;

    private final RemotingMessageSerializer serializer;

    public AbstractAction(final RequestContext context) {
        this.context = context;
        this.serializer = ServiceProvider.getInstance().getBean(RemotingMessageSerializer.class);
    }

    public RequestContext getRequestContext() {
        return context;
    }


    public <T> T buildRequestMessage(Class<T> requestBodyClass) {
        return serializer.deserialize(context.rawRequestBody(), requestBodyClass);
    }

    public RemotingMessage buildResponseMessage(ResponseBody responseBody) {
        byte[] body = serializer.serialize(responseBody);
        Header header = Header.builder()
                .crcCode(context.requestHeader().getCrcCode())
                .type(context.requestHeader().getType())
                .version(context.requestHeader().getVersion())
                .length(body.length)
                .build();
        return RemotingMessage
                .builder()
                .header(header)
                .body(body)
                .build();
    }

    public HodorChannelFuture sendMessage(RemotingMessage message) {
        return this.context.channel().send(message);
    }

    public void sendMessageThenClose(RemotingMessage message) {
        this.context.channel().send(message).operationComplete(e -> e.channel().close());
    }

}
