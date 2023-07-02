package org.dromara.hodor.register.embedded.client;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcUtil;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.thirdparty.io.grpc.*;
import org.apache.ratis.thirdparty.io.grpc.stub.ClientCalls;
import org.apache.ratis.thirdparty.io.grpc.stub.StreamObserver;
import org.apache.ratis.util.IOUtils;
import org.dromara.hodor.common.proto.DataChangeEvent;
import org.dromara.hodor.common.proto.WatchRequest;
import org.dromara.hodor.common.proto.WatchResponse;
import org.dromara.hodor.common.proto.WatchServiceGrpc;
import org.dromara.hodor.common.utils.BytesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GrpcWatchClientRpc
 *
 * @author tomgs
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class GrpcWatchClientRpcV2 implements WatchClientRpc {

    private static final AtomicBoolean started = new AtomicBoolean();

    private static final BytesUtil.ByteArrayComparator byteArrayComparator = BytesUtil.getDefaultByteArrayComparator();

    private static final Map<byte[], WatchCallback> watchClientCallBackMap = Maps.newTreeMap(byteArrayComparator);

    private final Map<RaftPeerId, RaftPeer> peers = new ConcurrentHashMap<>();

    private StreamObserver<WatchRequest> watchRequestStreamObserver;

    private final ListeningScheduledExecutorService scheduledExecutor;

    private final long channelKeepAlive;

    private final int maxInboundMessageSize;

    private final ThreadPoolExecutor grpcExecutor;

    private final ClientId clientId;

    private final List<WatchRequest> watchRequestCahce;

    public GrpcWatchClientRpcV2(ClientId clientId, RaftProperties properties) {
        this.clientId = clientId;
        this.grpcExecutor = ThreadUtil.newExecutor(4, 4);
        this.grpcExecutor.setThreadFactory(ThreadUtil.newNamedThreadFactory("hodor-watch-client-", true));
        this.scheduledExecutor = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(2,
            ThreadUtil.newNamedThreadFactory("hodor-connect-client-", true)));
        this.channelKeepAlive = properties.getLong("hodor.watch.keepalive", 60 * 1000);
        this.maxInboundMessageSize = properties.getInt("hodor.watch.max.messageSize", 4096);
        this.watchRequestCahce = new ArrayList<>();
        this.scheduledExecutor.submit(this::startHandleWatchStream);
    }

    @Override
    public void watch(WatchRequest watchRequest, WatchCallback watchCallback) throws Exception {
        watchClientCallBackMap.put(watchRequest.getCreateRequest().getKey().toByteArray(), watchCallback);
        watchRequestCahce.add(watchRequest);
    }

    @Override
    public void unwatch(WatchRequest watchRequest) {
        watchClientCallBackMap.remove(watchRequest.getCancelRequest().getKey().toByteArray());
        watchRequestCahce.remove(watchRequest);
    }

    @Override
    public void close() throws IOException {
        if (started.compareAndSet(true, false)) {
            watchClientCallBackMap.clear();
            watchRequestCahce.clear();
        }
    }

    @Override
    public void addRaftPeers(Collection<RaftPeer> raftPeers) {
        for (RaftPeer p : raftPeers) {
            //peers.computeIfAbsent(p);
            peers.put(p.getId(), p);
        }
    }

    @Override
    public boolean shouldReconnect(Throwable e) {
        final Throwable cause = e.getCause();
        if (e instanceof IOException && cause instanceof StatusRuntimeException) {
            return !((StatusRuntimeException) cause).getStatus().isOk();
        } else if (e instanceof IllegalArgumentException) {
            return e.getMessage().contains("null frame before EOS");
        }
        return IOUtils.shouldReconnect(e);
    }

    public void startHandleWatchStream() {
        // 实现client的状态
        if (!started.compareAndSet(false, true)) {
            return;
        }
        if (peers.size() == 0) {
            throw new RuntimeException("peers is empty");
        }
        while (true) {
            try {
                sendWatchRequest(peers.values());
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    private void sendWatchRequest(Collection<RaftPeer> peers) {
        final Optional<RaftPeer> any = peers.stream().findAny();
        List<RaftPeer> list = new ArrayList<>(peers);
        list.add(0, any.get());
        for (RaftPeer peer : list) {
            final String address = peer.getAddress();
            final List<String> endpoint = StrUtil.splitTrim(address, ":");
            String host = endpoint.get(0);
            int port = Integer.parseInt(endpoint.get(1));
            ManagedChannel channel = createNewManagedChannel(host, port);
            final ClientCall<WatchRequest, WatchResponse> clientCall = channel.newCall(WatchServiceGrpc.getWatchMethod(), CallOptions.DEFAULT);
            this.watchRequestStreamObserver = ClientCalls.asyncBidiStreamingCall(clientCall, getResponseObserver());

            for (WatchRequest watchRequest : watchRequestCahce) {
                watchRequestStreamObserver.onNext(watchRequest);
            }

            if (channel != null) {
                GrpcUtil.shutdownManagedChannel(channel);
            }
        }
    }

    @NotNull
    private StreamObserver<WatchResponse> getResponseObserver() {
        return new StreamObserver<WatchResponse>() {
            @Override
            public void onNext(WatchResponse response) {
                try {
                    final DataChangeEvent event = response.getEvent();
                    log.info("watch event received, {}", event);

                    final Optional<byte[]> watchKeyOptional = getWatchKey(event.getKey().toByteArray());
                    watchKeyOptional.ifPresent(watchKey -> {
                        final WatchCallback watchCallback = watchClientCallBackMap.getOrDefault(watchKey,
                            changeEvent -> log.warn("not found event key, {}.", changeEvent.getKey()));
                        watchCallback.callback(event);
                    });
                } catch (Exception e) {
                    log.error("[{}] Error to process server push response: {}", clientId, response.getEvent().toString());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Watch error, {}", t.getMessage(), t);
                if (shouldReconnect(t)) {
                    log.error("Watch reconnect, {}", t.getMessage(), t);
                    started.compareAndSet(true, false);
                    addOnFailureLoggingCallback(
                        grpcExecutor,
                        scheduledExecutor.schedule(() -> startHandleWatchStream(), 500, TimeUnit.MILLISECONDS),
                        "Scheduled startHandleWatchStream failed"
                    );
                }
            }

            @Override
            public void onCompleted() {
                log.info("stream completed");
            }
        };
    }

    public Optional<byte[]> getWatchKey(byte[] key) {
        final Set<byte[]> keySet = watchClientCallBackMap.keySet();
        return keySet.stream().filter(k -> byteArrayComparator
                .compare(k, 0, k.length, key, 0, k.length) >= 0)
            .findFirst();
    }

    /**
     * create a new channel with specific server address.
     *
     * @param serverIp   serverIp.
     * @param serverPort serverPort.
     * @return if server check success,return a non-null channel.
     */
    private ManagedChannel createNewManagedChannel(String serverIp, int serverPort) {
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(serverIp, serverPort)
            .executor(grpcExecutor)
            .compressorRegistry(CompressorRegistry.getDefaultInstance())
            .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
            .maxInboundMessageSize(maxInboundMessageSize)
            //.keepAliveTime(channelKeepAlive, TimeUnit.MILLISECONDS)
            .keepAliveTimeout(channelKeepAlive, TimeUnit.MILLISECONDS)
            .usePlaintext();
        return managedChannelBuilder.build();
    }

    void addOnFailureLoggingCallback(
        Executor executor,
        ListenableFuture<?> listenableFuture,
        String message) {
        Futures.addCallback(
            listenableFuture,
            new FutureCallback<Object>() {
                @Override
                public void onFailure(@NotNull Throwable throwable) {
                    log.error(message, throwable);
                }

                @Override
                public void onSuccess(Object result) {
                }
            },
            executor
        );
    }
}
