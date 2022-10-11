package org.dromara.hodor.common.raft.kv.core;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.SimpleStateMachineStorage;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.thirdparty.io.netty.handler.codec.CodecException;
import org.dromara.hodor.common.raft.HodorRaftStateMachine;
import org.dromara.hodor.common.raft.kv.protocol.*;
import org.dromara.hodor.common.raft.kv.storage.DBStore;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;
import org.dromara.hodor.common.utils.BytesUtil;
import org.dromara.hodor.common.utils.ProtostuffUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * RatisServerStateMachine
 *
 * @author tomgs
 * @since 2022/3/22
 */
@Slf4j
public class HodorKVStateMachine extends HodorRaftStateMachine {

    private final SimpleStateMachineStorage storage =
            new SimpleStateMachineStorage();

    private final StorageEngine storageEngine;

    private final DBStore dbStore;

    private RaftGroupId raftGroupId;

    public HodorKVStateMachine(final StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
        this.dbStore = this.storageEngine.getRawDBStore();
    }

    @Override
    public void initialize(RaftServer raftServer, RaftGroupId id, RaftStorage raftStorage) throws IOException {
        getLifeCycle().startAndTransition(() -> {
            super.initialize(raftServer, raftGroupId, raftStorage);
            this.raftGroupId = id;
            storage.init(raftStorage);
        });
    }

    @Override
    public void reinitialize() throws IOException {
        super.reinitialize();
        /*getLifeCycle().startAndTransition(() -> {
            loadSnapshotInfoFromDB();
            this.ozoneManagerDoubleBuffer = buildDoubleBufferForRatis();
            handler.updateDoubleBuffer(ozoneManagerDoubleBuffer);
        });*/
    }

    @Override
    public long takeSnapshot() throws IOException {
        return super.takeSnapshot();
    }

    @Override
    public CompletableFuture<Message> applyTransaction(TransactionContext trx) {
        final ByteString logData = trx.getStateMachineLogEntry().getLogData();
        try {
            return CompletableFuture.completedFuture(runCommand(Message.valueOf(logData)));
        } catch (CodecException e) {
            return completeExceptionally(e);
        }
    }

    @Override
    public CompletableFuture<Message> query(Message request) {
        try {
            // do something
            return CompletableFuture.completedFuture(runCommand(request));
        } catch (Exception e) {
            return completeExceptionally(e);
        }
    }

    private Message runCommand(Message request) throws CodecException {
        HodorKVRequest kvRequest = ProtostuffUtils.deserialize(request.getContent().toByteArray(), HodorKVRequest.class);
        final CmdType cmdType = kvRequest.getCmdType();
        HodorKVResponse.HodorKVResponseBuilder builder = HodorKVResponse.builder()
                .requestId(kvRequest.getRequestId())
                .traceId(kvRequest.getTraceId())
                .cmdType(cmdType)
                .success(true);
        switch (cmdType) {
            case GET:
                log.info("GET op.");
                final GetRequest getRequest = kvRequest.getGetRequest();
                byte[] result = dbStore.get(getRequest.getKey());
                GetResponse getResponse = GetResponse.builder()
                        .value(result)
                        .build();
                builder.getResponse(getResponse);
                break;
            case PUT:
                log.info("PUT op.");
                final PutRequest putRequest = kvRequest.getPutRequest();
                try {
                    dbStore.put(putRequest.getKey(), putRequest.getValue());
                } catch (Exception e) {
                    log.error("PUT exception: {}", e.getMessage(), e);
                    builder.success(false)
                            .message(e.getMessage());
                }
                break;
            case DELETE:
                log.info("DELETE op.");
                final DeleteRequest deleteRequest = kvRequest.getDeleteRequest();
                try {
                    dbStore.delete(deleteRequest.getKey());
                } catch (Exception e) {
                    log.error("DELETE exception: {}", e.getMessage(), e);
                    builder.success(false)
                            .message(e.getMessage());
                }
                break;
            case CONTAINS_KEY:
                log.info("CONTAINS_KEY op.");
                final ContainsKeyRequest containsKeyRequest = kvRequest.getContainsKeyRequest();
                try {
                    final Boolean exists = dbStore.containsKey(containsKeyRequest.getKey());
                    ContainsKeyResponse containsKeyResponse = ContainsKeyResponse.builder()
                        .value(exists)
                        .build();
                    builder.containsKeyResponse(containsKeyResponse);
                } catch (Exception e) {
                    log.error("DELETE exception: {}", e.getMessage(), e);
                    builder.success(false)
                        .message(e.getMessage());
                }
                break;
            case SCAN:
                log.info("SCAN op.");
                final ScanRequest scanRequest = kvRequest.getScanRequest();
                final byte[] startKey = scanRequest.getStartKey();
                final byte[] endKey = scanRequest.getEndKey();
                try {
                    final List<KVEntry> kvEntries = dbStore.scan(startKey, endKey, scanRequest.isReturnValue());
                    ScanResponse scanResponse = ScanResponse.builder()
                        .value(kvEntries)
                        .build();
                    builder.scanResponse(scanResponse);
                } catch (Exception e) {
                    log.error("Fail to [SCAN], range: ['[{}, {})'], {}.", BytesUtil.toHex(startKey), BytesUtil.toHex(endKey),
                        ExceptionUtils.getStackTrace(e));
                    builder.success(false)
                        .message(e.getMessage());
                }
                break;
            default:
                throw new RuntimeException("Unsupported request type: " + request.getClass().getName());
        }
        return Message.valueOf(ByteString.copyFrom(ProtostuffUtils.serialize(builder.build())));
    }

    @Override
    public void close() throws IOException {
        super.close();
        storageEngine.close();
    }

    private static <T> CompletableFuture<T> completeExceptionally(Exception e) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(e);
        return future;
    }

}
