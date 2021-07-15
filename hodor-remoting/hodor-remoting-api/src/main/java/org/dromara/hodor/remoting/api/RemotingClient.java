package org.dromara.hodor.remoting.api;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * remoting client service
 *
 * @author tomgs
 * @since 2020/12/1
 */
@Slf4j
public class RemotingClient {

    public static final RemotingClient INSTANCE = new RemotingClient();

    private final Map<Host, HodorChannel> activeChannels = Maps.newConcurrentMap();

    private final NetClientTransport clientTransport;

    private RemotingClient() {
        this.clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
    }

    /**
     * send async request
     *
     * @param host server host
     * @param request request message
     * @throws RemotingException remoting exception
     */
    public void sendAsyncRequest(final Host host, final RemotingMessage request, final FutureCallback<RemotingMessage> callback) throws RemotingException {
        final CompletableFuture<RemotingMessage> future = sendRequest(host, request);
        future.thenAcceptAsync(callback::onSuccess)
            .exceptionally(e -> {
                callback.onFailure(e);
                return null;
            });
    }

    /**
     * send sync request
     *
     * @param host server host
     * @param request request message
     * @param timeout timeout milliseconds
     * @return response message
     */
    public RemotingMessage sendSyncRequest(final Host host, final RemotingMessage request, final int timeout)
            throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<RemotingMessage> future = sendRequest(host, request);
        return future.get(timeout, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<RemotingMessage> sendRequest(final Host host, final RemotingMessage request) {
        final CompletableFuture<RemotingMessage> future = new CompletableFuture<>();
        final HodorChannel channel = computeActiveChannel(host, e -> createChannel(e, new JobResponseHandler(future)));
        channel.send(request).operationComplete(e -> {
            if (!e.isSuccess()) {
                future.completeExceptionally(e.cause());
            }
        });
        return future;
    }

    public HodorChannel computeActiveChannel(final Host host, final Function<Host, HodorChannel> function) {
        HodorChannel hodorChannel = activeChannels.get(host);
        if (hodorChannel != null && hodorChannel.isOpen()) {
            return hodorChannel;
        }
        return function.apply(host);
    }

    public HodorChannel createChannel(final Host host, final HodorChannelHandler handler) {
        Attribute attribute = buildAttribute(host);
        // handle request
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

    private static class JobResponseHandler implements HodorChannelHandler {

        private final CompletableFuture<RemotingMessage> future;

        public JobResponseHandler(final CompletableFuture<RemotingMessage> future) {
            this.future = future;
        }

        @Override
        public void received(HodorChannel channel, Object message) {
            if (message instanceof RemotingMessage) {
                future.complete((RemotingMessage) message);
                return;
            }
            future.completeExceptionally(new IllegalArgumentException("response message is illegal, " + message));
        }

        @Override
        public void exceptionCaught(HodorChannel channel, Throwable cause) {
            future.completeExceptionally(cause);
        }

    }
}
