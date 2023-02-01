package org.dromara.hodor.register.embedded.client;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.conf.RaftProperties;
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

/**
 * GrpcWatchClientRpc
 *
 * @author tomgs
 * @version 1.0
 */
@Slf4j
public class GrpcWatchClientRpc implements WatchClientRpc {

    private StreamObserver<WatchRequest> watchRequestStreamObserver;

    private static final BytesUtil.ByteArrayComparator byteArrayComparator = BytesUtil.getDefaultByteArrayComparator();
    private static final Map<byte[], WatchCallback> watchClientCallBackMap = Maps.newTreeMap(byteArrayComparator);

    private final long channelKeepAlive = 60 * 1000;

    private final int maxInboundMessageSize = 4096;

    private ThreadPoolExecutor grpcExecutor;

    private final ClientId clientId;

    private RaftProperties properties;

    private final Map<RaftPeerId, RaftPeer> peers = new ConcurrentHashMap<>();

    public GrpcWatchClientRpc(ClientId clientId, RaftProperties properties) {
        this.clientId = clientId;
        this.properties = properties;
    }

    @Override
    public void watch(WatchRequest watchRequest, WatchCallback watchCallback) {
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
        //watchRequestStreamObserver.onCompleted();
        watchClientCallBackMap.clear();
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

    public void startHandleWatchStreamResponse() {
        if (peers.size() == 0) {
            throw new RuntimeException("perrs is empty");
        }
        for (RaftPeer peer : peers.values()) {
            try {
                final String address = peer.getAddress();
                final List<String> endpoint = StrUtil.splitTrim(address, ":");
                String host = endpoint.get(0);
                int port = Integer.parseInt(endpoint.get(1));
                final ManagedChannel channel = createNewManagedChannel(host, port);

                //final ConnectivityState state = channel.getState(true);
                //channel.notifyWhenStateChanged();
                final ClientCall<WatchRequest, WatchResponse> clientCall = channel.newCall(WatchServiceGrpc.getWatchMethod(), CallOptions.DEFAULT);
                this.watchRequestStreamObserver = ClientCalls.asyncBidiStreamingCall(clientCall, new StreamObserver<WatchResponse>() {
                    @Override
                    public void onNext(WatchResponse response) {
                        try {
                            final DataChangeEvent event = response.getEvent();
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
                        log.error("error: ", t);
                        // 判断是否需要重连
                        if (shouldReconnect(t)) {

                        }
                    }

                    @Override
                    public void onCompleted() {
                        log.info("stream completed");
                        // 判断是否需要重连
                    }
                });
                break;
            } catch (Exception e) {
                log.error("connection exception: {}", e.getMessage(), e);
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
        grpcExecutor = ThreadUtil.newExecutor(4, 4);
        grpcExecutor.setThreadFactory(ThreadUtil.newNamedThreadFactory("hodor-watch-", true));
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(serverIp, serverPort)
            .executor(grpcExecutor).compressorRegistry(CompressorRegistry.getDefaultInstance())
            .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
            .maxInboundMessageSize(maxInboundMessageSize)
            //.keepAliveTime(channelKeepAlive, TimeUnit.MILLISECONDS)
            .keepAliveTimeout(channelKeepAlive, TimeUnit.MILLISECONDS)
            .usePlaintext();
        return managedChannelBuilder.build();
    }

}