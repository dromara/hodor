package org.dromara.hodor.remoting.api.http;

import java.net.ConnectException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.NetClient;
import org.dromara.hodor.remoting.api.NetClientTransport;
import org.dromara.hodor.remoting.api.RemotingConst;

/**
 * hodor rest client
 *
 * @author tomgs
 * @since 2021/8/4
 */
@Slf4j
public class HodorRestClient {

    private static volatile HodorRestClient INSTANCE;

    private HodorRestClient() {

    }

    public static HodorRestClient getInstance() {
        if (INSTANCE == null) {
            synchronized (HodorRestClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HodorRestClient();
                }
            }
        }
        return INSTANCE;
    }

    public HodorHttpResponse sendSynHttRequest(final Host host, final HodorHttpRequest request, final Integer readTimeout) {
        CompletableFuture<HodorHttpResponse> future = sendHttpRequest(host, request);
        try {
            return future.get(readTimeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("send http request error, host: {}, body: {}, msg: {}", host, request, e.getMessage(), e);
        }
        return null;
    }

    public CompletableFuture<HodorHttpResponse> sendHttpRequest(final Host host, final HodorHttpRequest request) {
        return sendHttpRequest(host, request, 1000);
    }

    public CompletableFuture<HodorHttpResponse> sendHttpRequest(final Host host, final HodorHttpRequest request, final Integer connectTimeout) {
        Attribute attribute = buildAttribute(host, connectTimeout);
        CompletableFuture<HodorHttpResponse> responseFuture = new CompletableFuture<>();
        HodorRestClientHandler handler = new HodorRestClientHandler(request, responseFuture);
        // execute
        NetClientTransport clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
        NetClient client = clientTransport.build(attribute, handler);
        try {
            client.connect();
        } catch (ConnectException exception) {
            responseFuture.completeExceptionally(exception);
        }
        return responseFuture;
    }

    private Attribute buildAttribute(final Host host, final Integer connectTimeout) {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, host.getIp());
        attribute.put(RemotingConst.PORT_KEY, host.getPort());
        attribute.put(RemotingConst.HTTP_PROTOCOL, true);
        attribute.put(RemotingConst.NET_TIMEOUT_KEY, connectTimeout); // connect timeout 1000ms
        return attribute;
    }

    @Slf4j
    private static class HodorRestClientHandler implements HodorChannelHandler {

        private final CompletableFuture<HodorHttpResponse> responseFuture;

        private final HodorHttpRequest request;

        public HodorRestClientHandler(final HodorHttpRequest request, final CompletableFuture<HodorHttpResponse> responseFuture) {
            this.request = request;
            this.responseFuture = responseFuture;
        }

        @Override
        public void connected(HodorChannel channel) {
            channel.send(request);
        }

        @Override
        public void received(HodorChannel channel, Object message) {
            HodorHttpResponse httpResponse = (HodorHttpResponse) message;
            responseFuture.complete(httpResponse);
        }

        @Override
        public void exceptionCaught(HodorChannel channel, Throwable cause) {
            responseFuture.completeExceptionally(cause);
        }

    }


}
