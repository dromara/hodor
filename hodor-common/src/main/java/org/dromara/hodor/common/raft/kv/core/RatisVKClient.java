package org.dromara.hodor.common.raft.kv.core;

import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcFactory;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.dromara.hodor.common.raft.RaftGroupManager;
import org.dromara.hodor.common.raft.kv.exception.HodorKVClientException;
import org.dromara.hodor.common.raft.kv.protocol.*;
import org.dromara.hodor.common.storage.cache.CacheClient;
import org.dromara.hodor.common.storage.cache.CacheSourceConfig;
import org.dromara.hodor.common.utils.ProtostuffUtils;

import java.io.IOException;
import java.util.Optional;

import static org.dromara.hodor.common.raft.kv.core.RatisKVServer.RATIS_KV_GROUP_ID;

/**
 * RatisVKClient
 *
 * @author tomgs
 * @since 2022/3/22
 */
public class RatisVKClient<K, V> implements CacheClient<K, V> {

    private final RaftClient raftClient;

    private final CacheSourceConfig cacheSourceConfig;

    public RatisVKClient(final CacheSourceConfig cacheSourceConfig) {
        this.cacheSourceConfig = cacheSourceConfig;
        // create peers
        final String[] addresses = Optional.ofNullable(cacheSourceConfig.getServerAddresses())
                .map(s -> s.split(","))
                .orElse(null);
        if (addresses == null || addresses.length == 0) {
            throw new IllegalArgumentException("Failed to get " + cacheSourceConfig.getServerAddresses() + " from " + cacheSourceConfig);
        }

        final RaftGroup raftGroup = RaftGroupManager.getInstance().createRaftGroup(RATIS_KV_GROUP_ID, addresses);
        this.raftClient = buildClient(raftGroup);
    }

    private RaftClient buildClient(RaftGroup raftGroup) {
        RaftProperties raftProperties = new RaftProperties();
        RaftClient.Builder builder = RaftClient.newBuilder()
                .setProperties(raftProperties)
                .setRaftGroup(raftGroup)
                .setClientRpc(
                        new GrpcFactory(new Parameters())
                                .newRaftClientRpc(ClientId.randomId(), raftProperties));
        return builder.build();
    }

    private HodorKVResponse handleRatisKVReadRequest(HodorKVRequest request) throws IOException {
        final byte[] bytes = ProtostuffUtils.serialize(request);
        final RaftClientReply raftClientReply = raftClient.io().sendReadOnly(Message.valueOf(ByteString.copyFrom(bytes)));
        return ProtostuffUtils.deserialize(raftClientReply.getMessage().getContent().toByteArray(), HodorKVResponse.class);
    }

    private HodorKVResponse handleRatisKVWriteRequest(HodorKVRequest request) throws IOException {
        final byte[] bytes = ProtostuffUtils.serialize(request);
        final RaftClientReply raftClientReply = raftClient.io().send(Message.valueOf(ByteString.copyFrom(bytes)));
        return ProtostuffUtils.deserialize(raftClientReply.getMessage().getContent().toByteArray(), HodorKVResponse.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) {
        try {
            GetRequest getRequest = new GetRequest();
            getRequest.setKey(ProtostuffUtils.serialize(key));
            HodorKVRequest request = HodorKVRequest.builder()
                    .cmdType(CmdType.GET)
                    .requestId(123L)
                    .traceId(123L)
                    .getRequest(getRequest)
                    .build();
            HodorKVResponse response = handleRatisKVReadRequest(request);
            if (response.getSuccess()) {
                final byte[] value = response.getGetResponse().getValue();
                if (value == null) {
                    return null;
                }
                return (V) ProtostuffUtils.deserialize(value, cacheSourceConfig.getValueSederClass());
            } else {
                throw new HodorKVClientException(response.getMessage());
            }
        } catch (Exception e) {
            throw new HodorKVClientException(e.getMessage(), e);
        }
    }

    @Override
    public void put(K key, V value) {
        try {
            PutRequest putRequest = new PutRequest();
            putRequest.setKey(ProtostuffUtils.serialize(key));
            putRequest.setValue(ProtostuffUtils.serialize(value));
            HodorKVRequest request = HodorKVRequest.builder()
                    .cmdType(CmdType.PUT)
                    .requestId(123L)
                    .traceId(123L)
                    .putRequest(putRequest)
                    .build();
            HodorKVResponse response = handleRatisKVWriteRequest(request);
            if (!response.getSuccess()) {
                throw new HodorKVClientException(response.getMessage());
            }
        } catch (Exception e) {
            throw new HodorKVClientException(e.getMessage(), e);
        }
    }

    @Override
    public void put(K key, V value, int expire) {

    }

    @Override
    public void delete(K key) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest();
            deleteRequest.setKey(ProtostuffUtils.serialize(key));
            HodorKVRequest request = HodorKVRequest.builder()
                    .cmdType(CmdType.DELETE)
                    .requestId(123L)
                    .traceId(123L)
                    .deleteRequest(deleteRequest)
                    .build();
            HodorKVResponse response = handleRatisKVWriteRequest(request);
            if (!response.getSuccess()) {
                throw new HodorKVClientException(response.getMessage());
            }
        } catch (Exception e) {
            throw new HodorKVClientException(e.getMessage(), e);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void close() {

    }

}
