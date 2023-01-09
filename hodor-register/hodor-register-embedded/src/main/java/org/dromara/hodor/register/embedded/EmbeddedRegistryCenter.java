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

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.kv.core.KVConstant;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.dromara.hodor.common.utils.ProtostuffUtils;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.register.api.DataChangeEvent;
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
    }

    @Override
    public void close() throws IOException {
        this.watchClient.close();
    }

    @Override
    public boolean checkExists(String key) {
        return this.watchClient.getKvClient().containsKey(ProtostuffUtils.serialize(key));
    }

    @Override
    public String get(String key) {
        final byte[] values = this.watchClient.getKvClient().get(ProtostuffUtils.serialize(key));
        return ProtostuffUtils.deserialize(values, String.class);
    }

    @Override
    public List<String> getChildren(String key) {
        List<KVEntry> result = this.watchClient.getKvClient().scan(ProtostuffUtils.serialize(key), null, false);
        return Optional.ofNullable(result)
            .orElse(Lists.newArrayList())
            .stream()
            .map(e -> {
                final String fullKey = ProtostuffUtils.deserialize(e.getKey(), String.class);
                return StrUtil.removePrefix(fullKey, key);
            })
            .collect(Collectors.toList());
    }

    @Override
    public void createPersistent(String key, String value) {
        this.watchClient.getKvClient().put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(value));
    }

    @Override
    public void createEphemeral(String key, String value) {
        this.watchClient.getKvClient().put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(value));
    }

    @Override
    public void update(String key, String value) {
        this.watchClient.getKvClient().put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(value));
    }

    @Override
    public void remove(String key) {
        this.watchClient.getKvClient().delete(ProtostuffUtils.serialize(key));
    }

    @Override
    public void addDataCacheListener(String path, DataChangeListener listener) {
        this.watchClient.watch(ProtostuffUtils.serialize(path), listener);
    }

    @Override
    public void addConnectionStateListener(ConnectionStateChangeListener listener) {

    }

    @Override
    public void executeInLeader(String latchPath, LeaderExecutionCallback callback) {
        watchClient.leaderLatch(latchPath, callback);
    }

}
