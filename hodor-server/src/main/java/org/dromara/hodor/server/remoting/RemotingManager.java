package org.dromara.hodor.server.remoting;

import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * remoting manager
 *
 * @author tomgs
 * @since 2020/9/23
 */
public final class RemotingManager {

    private static final RemotingManager INSTANCE = new RemotingManager();

    private final Map<Host, HodorChannel> activeChannels = Maps.newConcurrentMap();
    private final NetClientTransport clientTransport;

    private RemotingManager() {
        this.clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
    }

    public static RemotingManager getInstance() {
        return INSTANCE;
    }

    public void sendRequest(final Host host, final RemotingRequest<? extends RequestBody> request) throws RemotingException {
        HodorChannel channel = getChannel(host);
        HodorChannelFuture hodorChannelFuture = channel.send(request);

    }

    public HodorChannel getChannel(final Host host) {
        HodorChannel hodorChannel = activeChannels.get(host);
        if (hodorChannel != null && hodorChannel.isOpen()) {
            return hodorChannel;
        }
        return createChannel(host);
    }

    public HodorChannel createChannel(final Host host) {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, host.getIp());
        attribute.put(RemotingConst.PORT_KEY, host.getPort());
        attribute.put(RemotingConst.TCP_PROTOCOL, true);
        // handle request
        @SuppressWarnings("unchecked")
        HodorChannelHandler handler = new JobResponseHandler((Class<? extends RemotingResponse<ResponseBody>>) RemotingResponse.class);
        NetClient client = clientTransport.connect(attribute, handler);
        HodorChannel hodorChannel = client.connection();
        activeChannels.put(host, hodorChannel);
        return hodorChannel;
    }

}
