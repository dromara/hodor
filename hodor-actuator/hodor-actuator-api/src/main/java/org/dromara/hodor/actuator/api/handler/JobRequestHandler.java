package org.dromara.hodor.actuator.api.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.actuator.api.executor.RequestHandleManager;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * job execute request handler
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class JobRequestHandler implements HodorChannelHandler {

    private final RequestHandleManager requestHandleManager;

    private final RemotingMessageSerializer remotingMessageSerializer;

    public JobRequestHandler(final RequestHandleManager requestHandleManager, final RemotingMessageSerializer remotingMessageSerializer) {
        //this.requestHandleManager = RequestHandleManager.getInstance();
        //this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        this.requestHandleManager = requestHandleManager;
        this.remotingMessageSerializer = remotingMessageSerializer;
    }

    @Override
    public void received(HodorChannel channel, Object message) {
        final RemotingMessage request = (RemotingMessage) message;
        final RequestContext context = new RequestContext(channel, request, remotingMessageSerializer);
        this.requestHandleManager.notifyRequestHandler(context);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.error("handler the request message has exception, message: {}.", cause.getMessage(), cause);
        channel.close();
    }

}
