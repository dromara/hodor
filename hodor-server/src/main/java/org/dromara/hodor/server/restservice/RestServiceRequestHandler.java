package org.dromara.hodor.server.restservice;

import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * rest service handler
 *
 * @author tomgs
 * @since 2021/1/27
 */
@Deprecated
public class RestServiceRequestHandler implements HodorChannelHandler {

    @Override
    public void send(HodorChannel channel, Object message) {

    }

    @Override
    public void received(HodorChannel channel, Object message) throws Exception {

    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {

    }

}
