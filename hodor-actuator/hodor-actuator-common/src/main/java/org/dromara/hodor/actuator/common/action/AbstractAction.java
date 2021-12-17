package org.dromara.hodor.actuator.common.action;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.remoting.api.message.RequestContext;
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

    private final RequestHandleManager requestHandleManager;

    public AbstractAction(final RequestContext context, final RequestHandleManager requestHandleManager) {
        Assert.notNull(context, "context must be not null.");
        Assert.notNull(context.serializer(), "serializer must be not null.");
        Assert.notNull(requestHandleManager, "requestHandleManager must be not null.");

        this.context = context;
        this.serializer = context.serializer();
        this.requestHandleManager = requestHandleManager;
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
        retryableSendMessage(RemotingResponse.succeeded(response));
    }

    @Override
    public void exceptionCaught(Exception e) {
        // send failed execute response
        log.error("execute has exception, {}.", e.getMessage(), e);
        RemotingResponse<O> response = RemotingResponse.failed(ThreadUtils.getStackTraceInfo(e));
        retryableSendMessage(response);
    }

    public void retryableSendMessage(RemotingResponse<O> response) {
        requestHandleManager.retryableSendMessage(this.context, response);
    }

    private <T> T buildRequestMessage(Class<T> requestBodyClass) {
        return serializer.deserialize(context.rawRequestBody(), requestBodyClass);
    }

}
