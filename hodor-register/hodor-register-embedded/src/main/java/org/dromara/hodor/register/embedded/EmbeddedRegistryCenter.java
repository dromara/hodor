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

package org.dromara.hodor.register.embedded;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.kv.core.KVConstant;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.dromara.hodor.common.raft.kv.storage.DBColumnFamily;
import org.dromara.hodor.common.utils.BytesUtil;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;
import org.dromara.hodor.register.api.exception.RegistryException;
import org.dromara.hodor.register.embedded.core.WatchManager;
import org.dromara.hodor.register.embedded.watch.HodorWatchClient;

/**
 * EmbeddedRegistryCenter
 *
 * @author tomgs
 * @since 2022/3/29
 */
@Join
@Slf4j
public class EmbeddedRegistryCenter implements RegistryCenter {

    private HodorWatchClient watchClient;

    private String tableName;

    @Override
    public void init(RegistryConfig config) throws Exception {
        final EmbeddedRegistryServer embeddedRegistryServer = new EmbeddedRegistryServer(config);
        embeddedRegistryServer.init();
        embeddedRegistryServer.start();
        // init client
        HodorRaftGroup hodorRaftGroup = HodorRaftGroup.builder()
            .raftGroupName(KVConstant.HODOR_KV_GROUP_NAME)
            .addresses(config.getServers())
            .build();
        this.watchClient = new HodorWatchClient(hodorRaftGroup);
        this.tableName = DBColumnFamily.HodorWatch.getName();
    }

    @Override
    public void close() throws Exception {
        this.watchClient.close();
    }

    @Override
    public boolean checkExists(String key) {
        return this.watchClient.getKvClient()
            .kvOperator(tableName)
            .containsKey(BytesUtil.writeUtf8(key));
    }

    @Override
    public String get(String key) {
        final byte[] values = this.watchClient.getKvClient()
            .kvOperator(tableName)
            .get(BytesUtil.writeUtf8(key));
        if (values == null) {
            return null;
        }
        return BytesUtil.readUtf8(values);
    }

    @Override
    public List<String> getChildren(String key) {
        final byte[] keyBytes = BytesUtil.writeUtf8(key);
        List<KVEntry> result = this.watchClient.getKvClient()
            .kvOperator(tableName)
            .scan(keyBytes, keyBytes, false);
        return Optional.ofNullable(result)
            .orElse(Lists.newArrayList())
            .stream()
            .map(e -> {
                final String fullKey = BytesUtil.readUtf8(e.getKey());
                return StrUtil.removePrefix(StrUtil.removePrefix(fullKey, key), "/");
            })
            .collect(Collectors.toList());
    }

    @Override
    public void createPersistent(String key, String value) {
        this.watchClient.getKvClient()
            .kvOperator(tableName)
            .put(BytesUtil.writeUtf8(key), BytesUtil.writeUtf8(value));
    }

    @Override
    public void createEphemeral(String key, String value) {
        this.watchClient.getKvClient()
            .kvOperator(tableName)
            .put(BytesUtil.writeUtf8(key), BytesUtil.writeUtf8(value));
    }

    @Override
    public void update(String key, String value) {
        this.watchClient.getKvClient()
            .kvOperator(tableName)
            .put(BytesUtil.writeUtf8(key), BytesUtil.writeUtf8(value));
    }

    @Override
    public void remove(String key) {
        this.watchClient.getKvClient()
            .kvOperator(tableName)
            .delete(BytesUtil.writeUtf8(key));
    }

    @Override
    public void addDataCacheListener(String path, DataChangeListener listener) {
        this.watchClient.watch(BytesUtil.writeUtf8(path), listener);
    }

    @Override
    public void addConnectionStateListener(ConnectionStateChangeListener listener) {

    }

    @Override
    public void executeInLeader(String latchPath, LeaderExecutionCallback callback) {
        leaderLatch(latchPath, callback);
    }

    private void leaderLatch(String latchPath, LeaderExecutionCallback callback) {
        if (WatchManager.getInstance().isLeader()) {
            try {
                leaderCallback(latchPath, callback);
            } catch (Exception e) {
                throw new RegistryException(e);
            }
        }
        WatchManager.getInstance().addListener(event -> {
            try {
                final org.dromara.hodor.common.proto.DataChangeEvent dataChangeEvent = event.getValue();
                if (dataChangeEvent.getType() == org.dromara.hodor.common.proto.DataChangeEvent.Type.NODE_ADDED) {
                    leaderCallback(latchPath, callback);
                } else if (dataChangeEvent.getType() == org.dromara.hodor.common.proto.DataChangeEvent.Type.NODE_REMOVED) {
                    this.watchClient.getKvClient()
                        .kvOperator(tableName)
                        .delete(BytesUtil.writeUtf8(latchPath));
                }
            } catch (Exception e) {
                throw new RegistryException(e);
            }
        }, org.dromara.hodor.common.proto.DataChangeEvent.Type.INITIALIZED);
    }

    private void leaderCallback(String latchPath, LeaderExecutionCallback callback) throws Exception {
        this.watchClient.getKvClient()
            .kvOperator(tableName)
            .delete(BytesUtil.writeUtf8(latchPath));
        callback.execute();
        this.watchClient.getKvClient()
            .kvOperator(tableName)
            .put(BytesUtil.writeUtf8(latchPath), BytesUtil.writeUtf8(DateUtil.now()));
    }

}
