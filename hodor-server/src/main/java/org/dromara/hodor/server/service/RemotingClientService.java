package org.dromara.hodor.server.service;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelFuture;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.NetClient;
import org.dromara.hodor.remoting.api.NetClientTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.RemotingRequest;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.server.executor.handler.JobResponseHandler;
import org.springframework.stereotype.Service;

/**
 * remoting client service
 *
 * @author tomgs
 * @since 2020/12/1
 */
@Slf4j
@Service
public class RemotingClientService {

    private final Map<Host, HodorChannel> activeChannels = Maps.newConcurrentMap();

    private final NetClientTransport clientTransport;

    public RemotingClientService() {
        this.clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
    }

    public void sendRequest(final Host host, final RemotingRequest<? extends RequestBody> request) throws RemotingException {
        HodorChannel channel = getChannel(host);
        HodorChannelFuture hodorChannelFuture = channel.send(request);
        if (hodorChannelFuture.isSuccess()) {
            log.debug("send request [{}]::[{}] success.", host.getEndpoint(), request);
        } else {
            String msg = String.format("send request [%s]::[%s] failed.", host.getEndpoint(), request);
            throw new RemotingException(msg, hodorChannelFuture.cause());
        }
    }

    public HodorChannel getChannel(final Host host) {
        HodorChannel hodorChannel = activeChannels.get(host);
        if (hodorChannel != null && hodorChannel.isOpen()) {
            return hodorChannel;
        }
        return createChannel(host);
    }

    public HodorChannel createChannel(final Host host) {
        Attribute attribute = buildAttribute(host);
        // handle request
        HodorChannelHandler handler = new JobResponseHandler();
        NetClient client = clientTransport.connect(attribute, handler);
        HodorChannel hodorChannel = client.connection();
        activeChannels.put(host, hodorChannel);
        return hodorChannel;
    }

    public Attribute buildAttribute(final Host host) {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, host.getIp());
        attribute.put(RemotingConst.PORT_KEY, host.getPort());
        attribute.put(RemotingConst.TCP_PROTOCOL, true);
        attribute.put(RemotingConst.NET_TIMEOUT_KEY, 100); // connect timeout 100ms
        return attribute;
    }

}
