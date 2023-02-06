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

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.dromara.hodor.common.proto.DataChangeEvent;
import org.dromara.hodor.common.raft.kv.core.HodorKVRequestHandler;
import org.dromara.hodor.common.raft.kv.protocol.CmdType;
import org.dromara.hodor.common.raft.kv.protocol.DeleteRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;
import org.dromara.hodor.common.raft.kv.protocol.PutRequest;
import org.dromara.hodor.common.raft.kv.storage.DBColumnFamily;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;
import org.dromara.hodor.register.embedded.core.WatchManager;

/**
 * HodorWatchRequestHandler
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorWatchRequestHandler extends HodorKVRequestHandler {

    private final WatchManager watchManager;

    public HodorWatchRequestHandler(final StorageEngine storageEngine) {
        super(storageEngine);
        this.watchManager = WatchManager.getInstance();
    }

    @Override
    public HodorKVResponse handleReadRequest(HodorKVRequest kvRequest) throws IOException {
        return super.handleReadRequest(kvRequest);
    }

    @Override
    public HodorKVResponse handleWriteRequest(HodorKVRequest kvRequest, long transactionLogIndex) throws IOException {
        final HodorKVResponse hodorKVResponse = super.handleWriteRequest(kvRequest, transactionLogIndex);
        if (!hodorKVResponse.getSuccess()
            || !DBColumnFamily.HodorWatch.getName().equals(kvRequest.getTable())) {
            return hodorKVResponse;
        }

        final CmdType cmdType = kvRequest.getCmdType();
        switch (cmdType) {
            case PUT:
                final PutRequest putRequest = kvRequest.getPutRequest();
                if (watchManager.containsWatchKey(putRequest.getKey())) {
                    final DataChangeEvent dataChangeEvent = DataChangeEvent.newBuilder()
                        .setKey(ByteString.copyFrom(putRequest.getKey()))
                        .setType(DataChangeEvent.Type.NODE_UPDATED)
                        .setData(ByteString.copyFrom(putRequest.getValue()))
                        .build();
                    watchManager.notify(dataChangeEvent);
                }
                break;
            case DELETE:
                final DeleteRequest deleteRequest = kvRequest.getDeleteRequest();
                if (watchManager.containsWatchKey(deleteRequest.getKey())) {
                    final DataChangeEvent dataChangeEvent = DataChangeEvent.newBuilder()
                        .setKey(ByteString.copyFrom(deleteRequest.getKey()))
                        .setType(DataChangeEvent.Type.NODE_REMOVED)
                        .setData(ByteString.EMPTY)
                        .build();
                    watchManager.notify(dataChangeEvent);
                }
                break;
        }
        return hodorKVResponse;
    }

}
