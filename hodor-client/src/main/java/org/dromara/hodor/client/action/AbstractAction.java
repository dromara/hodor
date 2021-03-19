package org.dromara.hodor.client.action;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.remoting.api.HodorChannelFuture;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * abstract action
 *
 * @author tomgs
 * @since 2021/3/4
 */
@Slf4j
public abstract class AbstractAction<I extends RequestBody, O extends ResponseBody> extends HodorRunnable {

    private final RequestContext context;

    private final RemotingMessageSerializer serializer;

    private Long requestId;

    public AbstractAction(final RequestContext context) {
        this.context = context;
        this.serializer = ServiceProvider.getInstance().getBean(RemotingMessageSerializer.class);
    }

    public RequestContext getRequestContext() {
        return context;
    }

    public abstract O executeRequest(I request) throws Exception;

    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws Exception {
        I request = (I) buildRequestMessage(getRequestContext().getRequestType());
        requestId = request.getRequestId();
        O response = executeRequest(request);
        response.setRequestId(requestId);
        sendMessage(buildResponseMessage(RemotingResponse.succeeded(requestId, response)));
    }

    @Override
    public void exceptionCaught(Exception e) {
        // send failed execute response
        log.error("execute has exception, {}.", e.getMessage(), e);
        RemotingResponse response = RemotingResponse.failed(requestId, e.getMessage(), ThreadUtils.getStackTraceInfo(e));
        sendMessage(buildResponseMessage(response));
    }

    public <T> T buildRequestMessage(Class<T> requestBodyClass) {
        return serializer.deserialize(context.rawRequestBody(), requestBodyClass);
    }

    public RemotingMessage buildResponseMessage(RemotingResponse response) {
        byte[] body = serializer.serialize(response);
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
