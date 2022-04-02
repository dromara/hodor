package org.dromara.hodor.common.raft.kv.core;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.BaseStateMachine;
import org.apache.ratis.statemachine.impl.SimpleStateMachineStorage;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.thirdparty.io.netty.handler.codec.CodecException;
import org.dromara.hodor.common.raft.kv.protocol.CmdType;
import org.dromara.hodor.common.raft.kv.protocol.DeleteRequest;
import org.dromara.hodor.common.raft.kv.protocol.GetRequest;
import org.dromara.hodor.common.raft.kv.protocol.GetResponse;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;
import org.dromara.hodor.common.raft.kv.protocol.PutRequest;
import org.dromara.hodor.common.raft.kv.serialization.ProtostuffUtils;
import org.dromara.hodor.common.raft.kv.storage.DBStore;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;

/**
 * RatisServerStateMachine
 *
 * @author tomgs
 * @since 2022/3/22
 */
@Slf4j
public class RatisKVServerStateMachine extends BaseStateMachine {

    private final SimpleStateMachineStorage storage =
            new SimpleStateMachineStorage();

    private final StorageEngine storageEngine;

    private final DBStore dbStore;

    private RaftGroupId raftGroupId;

    public RatisKVServerStateMachine(final StorageEngine storageEngine) {
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
        /*
         *   OMResponse response = handler.handleReadRequest(request);
         *   return OMRatisHelper.convertResponseToMessage(response);
         *   //
         *   public static Message convertResponseToMessage(OMResponse response) {
         *       byte[] requestBytes = response.toByteArray();
         *       return Message.valueOf(ByteString.copyFrom(requestBytes));
         *   }
         */
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
