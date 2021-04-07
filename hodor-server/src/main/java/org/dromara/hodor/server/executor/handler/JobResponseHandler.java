package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * job response handler
 *
 * @author tomgs
 * @since 2020/11/30
 */
@Slf4j
public class JobResponseHandler extends AbstractHodorClientChannelHandler {

    @Override
    protected void received0(HodorChannel channel, RemotingMessage message) {
        log.info("response message: {}.", message);
        ResponseHandlerManager.INSTANCE.fireJobResponseHandler(message);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        super.exceptionCaught(channel, cause);
    }

}
