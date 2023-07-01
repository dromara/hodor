package org.dromara.hodor.register.embedded.client;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcUtil;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.thirdparty.io.grpc.CallOptions;
import org.apache.ratis.thirdparty.io.grpc.ClientCall;
import org.apache.ratis.thirdparty.io.grpc.CompressorRegistry;
import org.apache.ratis.thirdparty.io.grpc.DecompressorRegistry;
import org.apache.ratis.thirdparty.io.grpc.ManagedChannel;
import org.apache.ratis.thirdparty.io.grpc.ManagedChannelBuilder;
import org.apache.ratis.thirdparty.io.grpc.StatusRuntimeException;
import org.apache.ratis.thirdparty.io.grpc.stub.ClientCalls;
import org.apache.ratis.thirdparty.io.grpc.stub.StreamObserver;
import org.apache.ratis.util.IOUtils;
import org.dromara.hodor.common.proto.DataChangeEvent;
import org.dromara.hodor.common.proto.WatchRequest;
import org.dromara.hodor.common.proto.WatchResponse;
import org.dromara.hodor.common.proto.WatchServiceGrpc;
import org.dromara.hodor.common.utils.BytesUtil;
import org.jetbrains.annotations.NotNull;

/**
 * GrpcWatchClientRpc
 *
 * @author tomgs
 * @version 1.0
 */
@Slf4j
public class GrpcWatchClientRpc implements WatchClientRpc {

    private static final AtomicBoolean started = new AtomicBoolean();

    private static final BytesUtil.ByteArrayComparator byteArrayComparator = BytesUtil.getDefaultByteArrayComparator();

    private static final Map<byte[], WatchCallback> watchClientCallBackMap = Maps.newTreeMap(byteArrayComparator);

    private final Map<RaftPeerId, RaftPeer> peers = new ConcurrentHashMap<>();

    private StreamObserver<WatchRequest> watchRequestStreamObserver;

    private final ListeningScheduledExecutorService executor;

    private final long channelKeepAlive;

    private final int maxInboundMessageSize;

    private final ThreadPoolExecutor grpcExecutor;

    private final ClientId clientId;

    public GrpcWatchClientRpc(ClientId clientId, RaftProperties properties) {
        this.clientId = clientId;
        this.grpcExecutor = ThreadUtil.newExecutor(4, 4);
        this.grpcExecutor.setThreadFactory(ThreadUtil.newNamedThreadFactory("hodor-watch-client-", true));
        this.executor = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1));
        this.channelKeepAlive = properties.getLong("hodor.watch.keepalive", 60 * 1000);
        this.maxInboundMessageSize = properties.getInt("hodor.watch.max.messageSize", 4096);
    }

    @Override
    public void watch(WatchRequest watchRequest, WatchCallback watchCallback) {
        startHandleWatchStream();
        watchRequestStreamObserver.onNext(watchRequest);
        watchClientCallBackMap.put(watchRequest.getCreateRequest().getKey().toByteArray(), watchCallback);
    }

    @Override
    public void unwatch(WatchRequest watchRequest) {
        watchRequestStreamObserver.onNext(watchRequest);
        watchClientCallBackMap.remove(watchRequest.getCancelRequest().getKey().toByteArray());
    }

    @Override
    public void close() throws IOException {
        if (started.compareAndSet(true, false)) {
            if (watchRequestStreamObserver != null) {
                watchRequestStreamObserver.onCompleted();
                watchRequestStreamObserver = null;
            }
            watchClientCallBackMap.clear();
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
        final Optional<RaftPeer> any = peers.values().stream().findAny();
        List<RaftPeer> list = new ArrayList<>(peers.values());
        list.add(0, any.get());
        for (RaftPeer peer : list) {
            ManagedChannel channel = null;
            try {
                final String address = peer.getAddress();
                final List<String> endpoint = StrUtil.splitTrim(address, ":");
                String host = endpoint.get(0);
                int port = Integer.parseInt(endpoint.get(1));
                channel = createNewManagedChannel(host, port);
                final ClientCall<WatchRequest, WatchResponse> clientCall = channel.newCall(WatchServiceGrpc.getWatchMethod(), CallOptions.DEFAULT);
                this.watchRequestStreamObserver = ClientCalls.asyncBidiStreamingCall(clientCall, new StreamObserver<WatchResponse>() {
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
                                executor.schedule(() -> startHandleWatchStream(), 500, TimeUnit.MILLISECONDS),
                                "Scheduled startHandleWatchStream failed"
                            );
                        }
                    }

                    @Override
                    public void onCompleted() {
                        log.info("stream completed");
                    }
                });
                break;
            } catch (Throwable e) {
                log.warn("connection exception: {}", e.getMessage(), e);
            }
            if (channel != null) {
                GrpcUtil.shutdownManagedChannel(channel);
            }
        }
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
