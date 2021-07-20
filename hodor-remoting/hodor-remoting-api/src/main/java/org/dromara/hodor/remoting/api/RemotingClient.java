package org.dromara.hodor.remoting.api;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.concurrent.FutureCallback;
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

    private static final Map<Long, CompletableFuture<RemotingMessage>> FUTURE_MAP = Maps.newConcurrentMap();

    private final Map<Host, HodorChannel> activeChannels = Maps.newConcurrentMap();

    private final NetClientTransport clientTransport;

    private RemotingClient() {
        this.clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
    }

    public void sendDuplexRequest(final Host host, final RemotingMessage request, final FutureCallback<RemotingMessage> callback) throws RemotingException {
        HodorChannel channel = computeIfInActiveChannel(host, e -> createChannel(e, new JobExecuteResponseHandler(callback)));
        HodorChannelFuture hodorChannelFuture = channel.send(request);
        hodorChannelFuture.operationComplete(future -> {
            if (future.isSuccess()) {
                log.debug("send request [{}]::[{}] success.", host.getEndpoint(), request);
            } else {
                // 异常节点
                channel.close();
                String msg = String.format("send request [%s]::[%s] failed.", host.getEndpoint(), request);
                throw new RemotingException(msg, future.cause());
            }
        });
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
        FUTURE_MAP.put(request.getHeader().getId(), future);
        final HodorChannel channel = computeIfInActiveChannel(host, e -> createChannel(e, new SyncResponseHandler()));
        channel.send(request).operationComplete(e -> {
            if (!e.isSuccess()) {
                FUTURE_MAP.remove(request.getHeader().getId());
                future.completeExceptionally(e.cause());
            }
        });
        return future;
    }

    public HodorChannel computeIfInActiveChannel(final Host host, final Function<Host, HodorChannel> function) {
        HodorChannel hodorChannel = activeChannels.get(host);
        if (hodorChannel != null && hodorChannel.isOpen()) {
            return hodorChannel;
        }
        return function.apply(host);
    }

    public HodorChannel createChannel(final Host host, final HodorChannelHandler handler) {
        Attribute attribute = buildAttribute(host);
        // handle request
        NetClient client = clientTransport.build(attribute, handler);
        HodorChannel hodorChannel = client.connect();
        activeChannels.put(host, hodorChannel);
        return hodorChannel;
    }

    public Attribute buildAttribute(final Host host) {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, host.getIp());
        attribute.put(RemotingConst.PORT_KEY, host.getPort());
        attribute.put(RemotingConst.TCP_PROTOCOL, true);
        attribute.put(RemotingConst.NET_TIMEOUT_KEY, 1000); // connect timeout 1000ms
        return attribute;
    }

    private static class SyncResponseHandler implements HodorChannelHandler {

        @Override
        public void received(HodorChannel channel, Object message) {
            if (!(message instanceof RemotingMessage)) {
                throw new IllegalArgumentException("response message is illegal, " + message);
            }
            final RemotingMessage remotingMessage = (RemotingMessage) message;
            try {
                CompletableFuture<RemotingMessage> future = FUTURE_MAP.get(remotingMessage.getHeader().getId());
                future.complete(remotingMessage);
            } finally {
                FUTURE_MAP.remove(remotingMessage.getHeader().getId());
            }
        }

        @Override
        public void exceptionCaught(HodorChannel channel, Throwable cause) {
            log.error("hodor client channel [{}] exception caught, msg: {}", channel.getId(), cause.getMessage(), cause);
            channel.close();
        }

    }

    private static class JobExecuteResponseHandler implements HodorChannelHandler {

        private final FutureCallback<RemotingMessage> callback;

        public JobExecuteResponseHandler(FutureCallback<RemotingMessage> callback) {
            this.callback = callback;
        }

        @Override
        public void received(HodorChannel channel, Object message) throws Exception {
            if (!(message instanceof RemotingMessage)) {
                throw new IllegalArgumentException("response message is illegal, " + message);
            }
            callback.onSuccess((RemotingMessage) message);
        }

        @Override
        public void exceptionCaught(HodorChannel channel, Throwable cause) {
            callback.onFailure(cause);
        }

    }

}
