package org.dromara.hodor.remoting.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.net.ConnectException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
 * @since 1.0
 */
@Slf4j
public class RemotingClient {

    private static final Map<Long, CompletableFuture<RemotingMessage>> FUTURE_MAP = new ConcurrentHashMap<>();

    private final Cache<Host, HodorChannel> bidiActiveChannels;
    //private final Map<Host, HodorChannel> bidiActiveChannels = new ConcurrentHashMap<>();

    private final Cache<Host, HodorChannel> commonActiveChannels;
    //private final Map<Host, HodorChannel> commonActiveChannels = new ConcurrentHashMap<>();

    private final NetClientTransport clientTransport;

    public RemotingClient() {
        this.bidiActiveChannels = CacheBuilder.newBuilder()
            .initialCapacity(30)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .removalListener(new ChannelRemoveListener())
            .build();
        this.commonActiveChannels = CacheBuilder.newBuilder()
            .initialCapacity(30)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .removalListener(new ChannelRemoveListener())
            .build();
        this.clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
    }

    public void sendBidiRequest(final Host host, final RemotingMessage request, final FutureCallback<RemotingMessage> responseCallback) throws RemotingException {
        HodorChannel channel = getOrCreateBidiChannel(host, responseCallback);
        HodorChannelFuture hodorChannelFuture = channel.send(request);
        hodorChannelFuture.operationComplete(future -> {
            if (future.isSuccess()) {
                log.debug("send request [{}]::[{}] success.", host.getEndpoint(), request);
            } else {
                // 异常节点
                channel.close();
                bidiActiveChannels.invalidate(host);
                throw new RemotingException(String.format("send request [%s]::[%s] failed.", host.getEndpoint(), request), future.cause());
            }
        });
    }

    /**
     * send async request
     *
     * @param host    server host
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
     * @param host    server host
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
        try {
            //final HodorChannel channel = createChannel(host, new CommonResponseHandler());
            final HodorChannel channel = getOrCreateCommonChannel(host);
            channel.send(request).operationComplete(e -> {
                if (!e.isSuccess()) {
                    FUTURE_MAP.remove(request.getHeader().getId());
                    future.completeExceptionally(e.cause());
                }
            });
        } catch (Exception e) {
            FUTURE_MAP.remove(request.getHeader().getId());
            future.completeExceptionally(e);
        }
        return future;
    }

    public HodorChannel getOrCreateBidiChannel(final Host host, FutureCallback<RemotingMessage> responseCallback) {
        HodorChannel hodorChannel = bidiActiveChannels.getIfPresent(host);
        if (hodorChannel != null && hodorChannel.isOpen()) {
            return hodorChannel;
        }
        final HodorChannel activeChannel = createChannel(host, new KeepAliveChannelResponseHandler(responseCallback));
        bidiActiveChannels.put(host, activeChannel);
        return activeChannel;
    }

    public HodorChannel getOrCreateCommonChannel(final Host host) {
        HodorChannel hodorChannel = commonActiveChannels.getIfPresent(host);
        if (hodorChannel != null && hodorChannel.isOpen()) {
            return hodorChannel;
        }
        final HodorChannel activeChannel = createChannel(host, new CommonResponseHandler());
        commonActiveChannels.put(host, activeChannel);
        return activeChannel;
    }

    public HodorChannel createChannel(final Host host, final HodorChannelHandler handler) {
        Attribute attribute = buildAttribute(host);
        NetClient client = clientTransport.build(attribute, handler);
        try {
            return client.connect();
        } catch (ConnectException exception) {
            throw new RemotingException(String.format("host %s connect exception", host), exception);
        }
    }

    private Attribute buildAttribute(final Host host) {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, host.getIp());
        attribute.put(RemotingConst.PORT_KEY, host.getPort());
        attribute.put(RemotingConst.TCP_PROTOCOL, true);
        attribute.put(RemotingConst.NET_TIMEOUT_KEY, 1000); // connect timeout 1000ms
        return attribute;
    }

    /**
     * 这种是适应于一应一答的方式
     */
    private static class CommonResponseHandler implements HodorChannelHandler {

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

    /**
     * 这种适应于长连接，双向通信
     */
    private static class KeepAliveChannelResponseHandler implements HodorChannelHandler {

        private final FutureCallback<RemotingMessage> callback;

        public KeepAliveChannelResponseHandler(FutureCallback<RemotingMessage> callback) {
            this.callback = callback;
        }

        @Override
        public void received(HodorChannel channel, Object message) {
            if (!(message instanceof RemotingMessage)) {
                throw new IllegalArgumentException("response message is illegal, " + message);
            }
            callback.onSuccess((RemotingMessage) message);
        }

        @Override
        public void exceptionCaught(HodorChannel channel, Throwable cause) {
            channel.close();
            callback.onFailure(cause);
        }

    }

    private static class ChannelRemoveListener implements RemovalListener<Host, HodorChannel> {

        @Override
        public void onRemoval(RemovalNotification<Host, HodorChannel> event) {
            final HodorChannel hodorChannel = event.getValue();
            if (hodorChannel == null || hodorChannel.isClose()) {
                return;
            }
            hodorChannel.close().operationComplete(e -> {
                if (e.isSuccess()) {
                    log.info("ChannelRemoveListener host {} remove", event.getKey());
                } else {
                    log.info("ChannelRemoveListener host {} remove failed, msg {}", event.getKey(), e.cause());
                }
            });
        }
    }

}
