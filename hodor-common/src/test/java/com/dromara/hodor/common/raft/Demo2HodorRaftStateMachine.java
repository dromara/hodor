/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dromara.hodor.common.raft;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftGroupMemberId;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.dromara.hodor.common.raft.HodorRaftStateMachine;

/**
 * DemoHodorRaftStateMachine
 *
 * @author tomgs
 * @since 1.0
 */
public class Demo2HodorRaftStateMachine extends HodorRaftStateMachine {

    @Override
    public void initialize(RaftServer raftServer, RaftGroupId raftGroupId, RaftStorage storage) throws IOException {
        super.initialize(raftServer, raftGroupId, storage);
    }

    @Override
    public void reinitialize() throws IOException {
        super.reinitialize();
    }

    @Override
    public CompletableFuture<Message> applyTransaction(TransactionContext trx) {
        return super.applyTransaction(trx);
    }

    @Override
    public CompletableFuture<Message> query(Message request) {
        return super.query(request);
    }

    @Override
    public void notifyLeaderChanged(RaftGroupMemberId groupMemberId, RaftPeerId newLeaderId) {
        System.out.println("Demo2HodorRaftStateMachine#notifyLeaderChanged ================================ " + groupMemberId + ":" + newLeaderId);
        RaftPeerId currentPeerId = groupMemberId.getPeerId();
        if (currentPeerId.equals(newLeaderId)) {
            System.out.println("LEADER");
        } else {
            System.out.println("FOLLOWER");
        }
    }

    @Override
    public void notifyNotLeader(Collection<TransactionContext> pendingEntries) throws IOException {
        // 不是主节点时回调
        System.out.println("Demo2HodorRaftStateMachine#notifyNotLeader ================================ " + pendingEntries);
    }
}
