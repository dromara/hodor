package org.dromara.hodor.common.raft.watch.client;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
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

/**
 * GrpcWatchClientRpc
 *
 * @author tomgs
 * @version 1.0
 */
@Slf4j
public class GrpcWatchClientRpc implements WatchClientRpc {

    private StreamObserver<WatchRequest> watchRequestStreamObserver;

    private static final Map<ByteString, WatchCallback> callBackMap = new ConcurrentHashMap<>();

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
        callBackMap.put(watchRequest.getCreateRequest().getKey(), watchCallback);
    }

    @Override
    public void unwatch(WatchRequest watchRequest) {
        watchRequestStreamObserver.onNext(watchRequest);
        callBackMap.remove(watchRequest.getCancelRequest().getKey());
    }

    @Override
    public void close() throws IOException {
        watchRequestStreamObserver.onCompleted();
        callBackMap.clear();
    }

    @Override
    public void addRaftPeers(Collection<RaftPeer> raftPeers) {
        for(RaftPeer p : raftPeers) {
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
                            final WatchCallback watchCallback = callBackMap.getOrDefault(event.getKey(), changeEvent -> log.debug("not found event key, {}.", changeEvent.getKey()));
                            watchCallback.callback(event);
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

    /**
     * create a new channel with specific server address.
     *
     * @param serverIp   serverIp.
     * @param serverPort serverPort.
     * @return if server check success,return a non-null channel.
     */
    private ManagedChannel createNewManagedChannel(String serverIp, int serverPort) {
        grpcExecutor = ThreadUtil.newExecutor(4, 4);
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(serverIp, serverPort)
                .executor(grpcExecutor).compressorRegistry(CompressorRegistry.getDefaultInstance())
                .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
                .maxInboundMessageSize(maxInboundMessageSize)
                .keepAliveTime(channelKeepAlive, TimeUnit.MILLISECONDS).usePlaintext();
        return managedChannelBuilder.build();
    }

}
