package org.dromara.hodor.common.raft.kv.core;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.protocol.TermIndex;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.StateMachineStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.SimpleStateMachineStorage;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.thirdparty.io.netty.handler.codec.CodecException;
import org.dromara.hodor.common.raft.HodorRaftStateMachine;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;
import org.dromara.hodor.common.utils.ProtostuffUtils;

/**
 * RatisServerStateMachine
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorKVStateMachine extends HodorRaftStateMachine {

    private final SimpleStateMachineStorage storage = new SimpleStateMachineStorage();

    private RaftGroupId raftGroupId;

    private final RequestHandler requestHandler;

    public HodorKVStateMachine(final RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
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
        final TermIndex lastAppliedTermIndex = getLastAppliedTermIndex();
        return lastAppliedTermIndex.getIndex();
        //return super.takeSnapshot();
    }

    @Override
    public StateMachineStorage getStateMachineStorage() {
        return storage;
    }

    @Override
    public CompletableFuture<Message> applyTransaction(TransactionContext trx) {
        try {
            final ByteString logData = trx.getStateMachineLogEntry().getLogData();
            final Message message = Message.valueOf(logData);
            final TermIndex termIndex = TermIndex.valueOf(trx.getLogEntry());
            HodorKVRequest kvRequest = ProtostuffUtils.deserialize(message.getContent().toByteArray(), HodorKVRequest.class);
            return CompletableFuture.completedFuture(runCommand(kvRequest, termIndex));
        } catch (Exception e) {
            return completeExceptionally(e);
        }
    }

    @Override
    public CompletableFuture<Message> query(Message request) {
        try {
            // do something
            HodorKVRequest kvRequest = ProtostuffUtils.deserialize(request.getContent().toByteArray(), HodorKVRequest.class);
            return CompletableFuture.completedFuture(runQueryCommand(kvRequest));
        } catch (Exception e) {
            return completeExceptionally(e);
        }
    }

    private Message runQueryCommand(HodorKVRequest kvRequest) {
        final HodorKVResponse hodorKVResponse = requestHandler.handleReadRequest(kvRequest);
        return Message.valueOf(ByteString.copyFrom(ProtostuffUtils.serialize(hodorKVResponse)));
    }

    private Message runCommand(HodorKVRequest kvRequest, TermIndex termIndex) throws CodecException, IOException {
        final long term = termIndex.getTerm();
        final long trxLogIndex = termIndex.getIndex();
        final HodorKVResponse hodorKVResponse = requestHandler.handleWriteRequest(kvRequest, trxLogIndex);
        updateLastAppliedTermIndex(termIndex);
        return Message.valueOf(ByteString.copyFrom(ProtostuffUtils.serialize(hodorKVResponse)));
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    private static <T> CompletableFuture<T> completeExceptionally(Exception e) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(e);
        return future;
    }

}
