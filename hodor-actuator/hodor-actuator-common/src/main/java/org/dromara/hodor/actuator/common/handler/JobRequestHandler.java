package org.dromara.hodor.actuator.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.actuator.common.executor.RequestHandleManager;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * job execute request handler
 *
 * @author tomgs
 * @since 2021/1/7
 */
@Slf4j
public class JobRequestHandler implements HodorChannelHandler {

    private final RequestHandleManager requestHandleManager;

    private final RemotingMessageSerializer serializer;

    public JobRequestHandler() {
        this.requestHandleManager = RequestHandleManager.getInstance();
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
    }

    @Override
    public void received(HodorChannel channel, Object message) {
        final RemotingMessage request = (RemotingMessage) message;
        final RequestContext context = new RequestContext(channel, request, serializer);
        this.requestHandleManager.notifyRequestHandler(context);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.error("handler the request message has exception, message: {}.", cause.getMessage(), cause);
        channel.close();
    }

}
