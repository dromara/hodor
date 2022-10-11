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
import org.dromara.hodor.common.raft.kv.core.HodorKVClient;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.dromara.hodor.common.utils.ProtostuffUtils;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;

/**
 * EmbeddedRegistryCenter
 *
 * @author tomgs
 * @since 2022/3/29
 */
@Join
@Slf4j
public class EmbeddedRegistryCenter implements RegistryCenter {

    private HodorKVClient kvClient;

    @Override
    public void init(RegistryConfig config) throws Exception {
        final EmbeddedRegistryServer embeddedRegistryServer = new EmbeddedRegistryServer(config);
        embeddedRegistryServer.init();
        embeddedRegistryServer.start();
        this.kvClient = new HodorKVClient(config.getServers());
    }

    @Override
    public void close() throws IOException {
        kvClient.close();
    }

    @Override
    public boolean checkExists(String key) {
        return kvClient.containsKey(ProtostuffUtils.serialize(key));
    }

    @Override
    public String get(String key) {
        final byte[] values = kvClient.get(ProtostuffUtils.serialize(key));
        return ProtostuffUtils.deserialize(values, String.class);
    }

    @Override
    public List<String> getChildren(String key) {
        List<KVEntry> result = kvClient.scan(ProtostuffUtils.serialize(key), null, false);
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
        kvClient.put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(value));
    }

    @Override
    public void createPersistentSequential(String key, String value) {
        throw new UnsupportedOperationException("Unsupported operation createPersistentSequential");
    }

    @Override
    public void createEphemeral(String key, String value) {
        kvClient.put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(value));
    }

    @Override
    public void createEphemeralSequential(String key, String value) {
        throw new UnsupportedOperationException("Unsupported operation createEphemeralSequential");
    }

    @Override
    public void update(String key, String value) {
        kvClient.put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(value));
    }

    @Override
    public void remove(String key) {
        kvClient.delete(ProtostuffUtils.serialize(key));
    }

    @Override
    public void addDataCacheListener(String path, DataChangeListener listener) {

    }

    @Override
    public void addConnectionStateListener(ConnectionStateChangeListener listener) {

    }

    @Override
    public void executeInLeader(String latchPath, LeaderExecutionCallback callback) {

    }

}
