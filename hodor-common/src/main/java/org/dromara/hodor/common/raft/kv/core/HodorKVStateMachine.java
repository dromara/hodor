package org.dromara.hodor.common.raft.kv.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.protocol.TermIndex;
import org.apache.ratis.server.raftlog.RaftLog;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.SnapshotInfo;
import org.apache.ratis.statemachine.StateMachineStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.SimpleStateMachineStorage;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.thirdparty.io.netty.handler.codec.CodecException;
import org.apache.ratis.util.LifeCycle;
import org.dromara.hodor.common.raft.HodorRaftStateMachine;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;
import org.dromara.hodor.common.raft.kv.storage.Table;
import org.dromara.hodor.common.utils.BytesUtil;
import org.dromara.hodor.common.utils.ProtostuffUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.dromara.hodor.common.raft.kv.core.KVConstant.TRANSACTION_INFO_KEY;

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

    private final HodorKVSnapshotInfo snapshotInfo;

    private final DBStoreHAManager dbStoreHAManager;

    public HodorKVStateMachine(final RequestHandler requestHandler,
                               final HodorKVSnapshotInfo snapshotInfo,
                               final DBStoreHAManager dbStoreHAManager) throws IOException {
        this.requestHandler = requestHandler;
        this.snapshotInfo = snapshotInfo;
        this.dbStoreHAManager = dbStoreHAManager;
        loadSnapshotInfoFromDB();
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
        if (getLifeCycleState() == LifeCycle.State.PAUSED) {
            getLifeCycle().startAndTransition(() -> {
                loadSnapshotInfoFromDB();
                this.setLastAppliedTermIndex(getLastAppliedTermIndex());
            });
        }
    }

    @Override
    public SnapshotInfo getLatestSnapshot() {
        LOG.debug("Latest Snapshot Info {}", snapshotInfo);
        return snapshotInfo;
    }

    @Override
    public long takeSnapshot() throws IOException {
        final TermIndex lastTermIndex = getLastAppliedTermIndex();

        if (lastTermIndex == null || lastTermIndex.getIndex() == RaftLog.INVALID_LOG_INDEX) {
            return -1;
        }
        long lastAppliedIndex = lastTermIndex.getIndex();
        // 更新快照信息
        snapshotInfo.updateTermIndex(lastTermIndex.getTerm(), lastAppliedIndex);
        // 将快照数据写入rocksdb
        TransactionInfo build = new TransactionInfo.Builder()
            .setTransactionIndex(lastAppliedIndex)
            .setCurrentTerm(lastTermIndex.getTerm()).build();
        Table<byte[], byte[]> txnInfoTable = dbStoreHAManager.getTransactionInfoTable();
        txnInfoTable.put(BytesUtil.writeUtf8(TRANSACTION_INFO_KEY), ProtostuffUtils.serialize(build));
        dbStoreHAManager.flushDB();
        return lastTermIndex.getIndex();
    }

    @Override
    public StateMachineStorage getStateMachineStorage() {
        return storage;
    }

    public void loadSnapshotInfoFromDB() throws IOException {
        // This is done, as we have a check in Ratis for not throwing
        // LeaderNotReadyException, it checks stateMachineIndex >= raftLog
        // nextIndex (placeHolderIndex).
        TransactionInfo transactionInfo =
            TransactionInfo.readTransactionInfo(dbStoreHAManager);
        if (transactionInfo != null) {
            setLastAppliedTermIndex(TermIndex.valueOf(
                transactionInfo.getTerm(),
                transactionInfo.getTransactionIndex()));
            snapshotInfo.updateTermIndex(transactionInfo.getTerm(),
                transactionInfo.getTransactionIndex());
        }
        LOG.info("LastAppliedIndex is set from TransactionInfo from OM DB as {}",
            getLastAppliedTermIndex());
    }

    @Override
    public CompletableFuture<TermIndex> notifyInstallSnapshotFromLeader(
        RaftProtos.RoleInfoProto roleInfoProto, TermIndex firstTermIndexInLog) {
        String leaderNodeId = RaftPeerId.valueOf(roleInfoProto.getFollowerInfo()
            .getLeaderInfo().getId().getId()).toString();
        LOG.info("Received install snapshot notification from OM leader: {} with " +
            "term index: {}", leaderNodeId, firstTermIndexInLog);

        // TODO: download snapshot from leader
        //CompletableFuture<TermIndex> future = CompletableFuture.supplyAsync(
        //    () -> ozoneManager.installSnapshotFromLeader(leaderNodeId),
        //    installSnapshotExecutor);
        //return future;
        return super.notifyInstallSnapshotFromLeader(roleInfoProto, firstTermIndexInLog);
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

    private Message runQueryCommand(HodorKVRequest kvRequest) throws IOException {
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
