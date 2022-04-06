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

import java.io.File;
import java.io.IOException;
import org.dromara.hodor.common.HodorLifecycle;
import org.dromara.hodor.common.raft.RaftOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVOptions;
import org.dromara.hodor.common.raft.kv.core.HodorKVServer;
import org.dromara.hodor.common.raft.kv.storage.StorageOptions;
import org.dromara.hodor.common.raft.kv.storage.StorageType;
import org.dromara.hodor.register.api.RegistryConfig;

/**
 * EmbeddedRegistryServer
 *
 * @author tomgs
 * @since 2022/4/6
 */
public class EmbeddedRegistryServer implements HodorLifecycle {

    private final HodorKVServer hodorKVServer;

    public EmbeddedRegistryServer(final RegistryConfig registryConfig) throws IOException {
        RaftOptions raftOptions = RaftOptions.builder()
            .endpoint(registryConfig.getEndpoint())
            .storageDir(new File(registryConfig.getDataPath()))
            .serverAddresses(registryConfig.getServers())
            //.stateMachineMap(stateMachineMap) // for customer
            .build();
        StorageOptions storageOptions = StorageOptions.builder()
            .storageType(StorageType.RocksDB)
            .storagePath(new File(registryConfig.getDataPath()))
            .build();
        HodorKVOptions kvOptions = HodorKVOptions.builder()
            .clusterName(registryConfig.getNamespace())
            .raftOptions(raftOptions)
            .storageOptions(storageOptions)
            .build();
        this.hodorKVServer = new HodorKVServer(kvOptions);
    }

    @Override
    public void start() throws Exception {
        hodorKVServer.start();
    }

    @Override
    public void stop() throws Exception {
        hodorKVServer.stop();
    }

}
