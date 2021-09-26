package org.dromara.hodor.remoting.netty;

import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.NetClient;
import org.dromara.hodor.remoting.api.NetClientTransport;

/**
 *  netty client transport
 *
 * @author tomgs
 * @version 2020/9/6 1.0 
 */
@Join
public class NettyClientTransport implements NetClientTransport {

    @Override
    public NetClient build(Attribute attribute, HodorChannelHandler handler) {
        return new NettyClient(attribute, handler);
    }

}
