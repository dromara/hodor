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

package org.dromara.hodor.common.raft.kv.core;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.client.RaftClient;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.RaftUtils;
import org.dromara.hodor.common.raft.kv.storage.DBColumnFamily;

/**
 * HodorKVClient
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorKVClient implements AutoCloseable {

    private final RaftClient raftClient;

    private final Map<String, KVOperator> tableNameMap;

    private final String clientId;

    public HodorKVClient(final HodorRaftGroup hodorRaftGroup) {
        this.raftClient = RaftUtils.createClient(hodorRaftGroup);
        this.tableNameMap = new HashMap<>();
        this.clientId = hodorRaftGroup.getRaftProperties().get(KVConstant.HODOR_CLIENT_ID);
    }

    public KVOperator kvOperator(String tableName) {
        return tableNameMap.computeIfAbsent(tableName, k -> new HodorKVOperator(clientId, tableName, raftClient));
    }

    public KVOperator defaultKvOperator() {
        return kvOperator(DBColumnFamily.Default.getName());
    }

    public RaftClient getRaftClient() {
        return this.raftClient;
    }

    @Override
    public void close() throws Exception {
        this.raftClient.close();
        this.tableNameMap.clear();
    }

}
