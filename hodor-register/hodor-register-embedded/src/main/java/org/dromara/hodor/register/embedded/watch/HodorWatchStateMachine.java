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

package org.dromara.hodor.register.embedded.watch;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.protocol.RaftGroupMemberId;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.proto.DataChangeEvent;
import org.dromara.hodor.common.raft.kv.core.HodorKVStateMachine;
import org.dromara.hodor.common.raft.kv.core.RequestHandler;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.register.embedded.core.WatchManager;

/**
 * HodorWatchStateMachine
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorWatchStateMachine extends HodorKVStateMachine {

    public HodorWatchStateMachine(final RequestHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    public void notifyLeaderChanged(RaftGroupMemberId groupMemberId, RaftPeerId newLeaderId) {
        log.debug("notifyLeaderChanged groupMemberId: {}, newLeaderId: {}.", groupMemberId, newLeaderId);
        RaftPeerId currentPeerId = groupMemberId.getPeerId();
        final DataChangeEvent.Builder dataChangeEventBuilder = DataChangeEvent.newBuilder()
            .setKey(ByteString.copyFrom(SchedulerNode.LATCH_PATH.getBytes(StandardCharsets.UTF_8)))
            .setData(currentPeerId.toByteString());
        if (currentPeerId.equals(newLeaderId)) {
            log.info("Current peer {} is LEADER", currentPeerId);
            dataChangeEventBuilder.setType(DataChangeEvent.Type.NODE_ADDED);
            WatchManager.getInstance().setLeader(true);
        } else {
            log.info("Current peer {} is FOLLOWER", currentPeerId);
            dataChangeEventBuilder.setType(DataChangeEvent.Type.NODE_REMOVED);
            WatchManager.getInstance().setLeader(false);
        }
        WatchManager.getInstance().publish(Event.create(dataChangeEventBuilder.build(), DataChangeEvent.Type.INITIALIZED));
    }

    @Override
    public void notifyNotLeader(Collection<TransactionContext> pendingEntries) {
        // 不是主节点时回调
        final ByteString id = this.getId().getRaftPeerIdProto().getId();
        log.debug("{} notifyNotLeader pendingEntries: {}", id, pendingEntries);
    }

}
