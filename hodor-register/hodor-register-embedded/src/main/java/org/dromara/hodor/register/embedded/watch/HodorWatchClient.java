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

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.client.RaftClientConfigKeys;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.retry.RetryPolicies;
import org.apache.ratis.retry.RetryPolicy;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.util.TimeDuration;
import org.dromara.hodor.common.proto.WatchCancelRequest;
import org.dromara.hodor.common.proto.WatchCreateRequest;
import org.dromara.hodor.common.proto.WatchRequest;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.kv.core.HodorKVClient;
import org.dromara.hodor.common.utils.BytesUtil;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.embedded.core.WatchRaftClient;

/**
 * HodorWatchClient
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorWatchClient implements Closeable {

    private final HodorKVClient kvClient;

    private final WatchRaftClient watchRaftClient;

    public HodorWatchClient(final HodorRaftGroup hodorRaftGroup) {
        this.kvClient = new HodorKVClient(hodorRaftGroup);
        this.watchRaftClient = buildWatchClient(hodorRaftGroup.getRaftGroup(), RetryPolicies.retryForeverNoSleep());
    }

    public HodorKVClient getKvClient() {
        return this.kvClient;
    }

    public WatchRaftClient buildWatchClient(RaftGroup raftGroup, RetryPolicy retryPolicy) {
        Parameters parameters = new Parameters();
        RaftProperties raftProperties = new RaftProperties();
        ClientId clientId = ClientId.randomId();
        RaftClientConfigKeys.Rpc.setRequestTimeout(raftProperties,
            TimeDuration.valueOf(90, TimeUnit.SECONDS));
        WatchRaftClient.Builder builder = WatchRaftClient.newBuilder()
            .setProperties(raftProperties)
            .setRaftGroup(raftGroup)
            .setRetryPolicy(retryPolicy)
            .setClientRpc(
                new WatchGrpcFactory(parameters)
                    .newRaftClientRpc(clientId, raftProperties))
            .setWatchClientRpc(
                new WatchGrpcFactory(parameters)
                    .newWatchClientRpc(clientId, raftProperties));
        return builder.build();
    }

    public void watch(byte[] watchKey, DataChangeListener dataChangeListener) {
        WatchCreateRequest createRequest = WatchCreateRequest.newBuilder()
            .setWatchId(123L)
            .setKey(ByteString.copyFrom(watchKey))
            .build();
        WatchRequest watchRequest = WatchRequest.newBuilder()
            .setNodeIdBytes(ByteString.copyFromUtf8("127.0.0.1"))
            .setCreateRequest(createRequest)
            .build();

        watchRaftClient.watchClientRpc().watch(watchRequest, changeEvent -> {
            try {
                final String dataKey = BytesUtil.readUtf8(changeEvent.getKey().toByteArray());
                final String dataValue = BytesUtil.readUtf8(changeEvent.getData().toByteArray());
                final org.dromara.hodor.common.proto.DataChangeEvent.Type type = changeEvent.getType();
                DataChangeEvent dataChangeEvent = new DataChangeEvent(type.name(), dataKey, dataValue.getBytes(StandardCharsets.UTF_8));
                dataChangeListener.dataChanged(dataChangeEvent);
            } catch (Exception e) {
                log.error("[{}]Error to process server push response: {}", createRequest.getWatchId(), changeEvent.toString());
            }
        });
    }

    public void unwatch(byte[] watchKey) {
        WatchCancelRequest createRequest = WatchCancelRequest.newBuilder()
            .setWatchId(123L)
            .setKey(ByteString.copyFrom(watchKey))
            .build();
        WatchRequest watchRequest = WatchRequest.newBuilder()
            .setNodeIdBytes(ByteString.copyFromUtf8("127.0.0.1"))
            .setCancelRequest(createRequest)
            .build();
        watchRaftClient.watchClientRpc().unwatch(watchRequest);
    }

    @Override
    public void close() throws IOException {
        this.kvClient.close();
        this.watchRaftClient.close();
    }

}
