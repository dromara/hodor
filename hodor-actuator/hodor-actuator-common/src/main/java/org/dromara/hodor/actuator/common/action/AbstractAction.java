package org.dromara.hodor.actuator.common.action;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.executor.RequestHandleManager;
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

    public AbstractAction(final RequestContext context) {
        this.context = context;
        this.serializer = context.serializer();
    }

    public RequestContext getRequestContext() {
        return context;
    }

    public abstract O executeRequest(I request) throws Exception;

    @Override
    @SuppressWarnings("unchecked")
    public void execute() throws Exception {
        I request = (I) buildRequestMessage(getRequestContext().getRequestType());
        Long requestId = request.getRequestId();
        O response = executeRequest(request);
        response.setRequestId(requestId);
        retryableSendMessage(buildResponseMessage(RemotingResponse.succeeded(response)));
    }

    @Override
    public void exceptionCaught(Exception e) {
        // send failed execute response
        log.error("execute has exception, {}.", e.getMessage(), e);
        RemotingResponse<O> response = RemotingResponse.failed(ThreadUtils.getStackTraceInfo(e));
        retryableSendMessage(buildResponseMessage(response));
    }

    public <T> T buildRequestMessage(Class<T> requestBodyClass) {
        return serializer.deserialize(context.rawRequestBody(), requestBodyClass);
    }

    public RemotingMessage buildResponseMessage(RemotingResponse<O> response) {
        byte[] body = serializer.serialize(response);
        Header header = Header.builder()
            .id(context.requestHeader().getId())
            .type(context.requestHeader().getType())
            .version(context.requestHeader().getVersion())
            .attachment(context.requestHeader().getAttachment())
            .length(body.length)
            .build();
        return RemotingMessage
            .builder()
            .header(header)
            .body(body)
            .build();
    }

    /**
     * 可重试消息发送
     *
     * @param message 消息体
     */
    public void retryableSendMessage(RemotingMessage message) {
        sendMessage(message).operationComplete(future -> {
            RequestHandleManager requestHandleManager = ServiceProvider.getInstance().getBean(RequestHandleManager.class);
            if (!future.isSuccess() || future.cause() != null) {
                log.warn("response failed.", future.cause());
                requestHandleManager.addRetrySendMessage(future.channel().remoteAddress(), message);
            } else {
                requestHandleManager.recordActiveChannel(future.channel());
            }
        });
    }

    public HodorChannelFuture sendMessage(RemotingMessage message) {
        return this.context.channel().send(message);
    }

    public void sendMessageThenClose(RemotingMessage message) {
        this.context.channel().send(message).operationComplete(e -> e.channel().close());
    }

}
