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
import org.dromara.hodor.common.raft.kv.core.KVConstant;
import org.dromara.hodor.common.utils.BytesUtil;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.embedded.core.WatchRaftClient;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * HodorWatchClient
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorWatchClient implements AutoCloseable {

    private final HodorKVClient kvClient;

    private final WatchRaftClient watchRaftClient;

    private final String endpoint;

    public HodorWatchClient(final HodorRaftGroup hodorRaftGroup) {
        this.kvClient = new HodorKVClient(hodorRaftGroup);
        this.watchRaftClient = buildWatchClient(hodorRaftGroup, RetryPolicies.retryForeverNoSleep());
        final RaftProperties raftProperties = hodorRaftGroup.getRaftProperties();
        this.endpoint = raftProperties.get(KVConstant.HODOR_CLIENT_ID);
    }

    public HodorKVClient getKvClient() {
        return this.kvClient;
    }

    public WatchRaftClient buildWatchClient(HodorRaftGroup hodorRaftGroup, RetryPolicy retryPolicy) {
        Parameters parameters = hodorRaftGroup.getParameters();
        RaftProperties raftProperties = hodorRaftGroup.getRaftProperties();
        ClientId clientId = ClientId.randomId();
        RaftClientConfigKeys.Rpc.setRequestTimeout(raftProperties,
            TimeDuration.valueOf(90, TimeUnit.SECONDS));
        WatchRaftClient.Builder builder = WatchRaftClient.newBuilder()
            .setProperties(raftProperties)
            .setRaftGroup(hodorRaftGroup.getRaftGroup())
            .setRetryPolicy(retryPolicy)
            .setClientId(clientId)
            .setClientRpc(
                new WatchGrpcFactory(parameters)
                    .newRaftClientRpc(clientId, raftProperties))
            .setWatchClientRpc(
                new WatchGrpcFactory(parameters)
                    .newWatchClientRpc(clientId, raftProperties));
        return builder.build();
    }

    public void watch(byte[] watchKey, DataChangeListener dataChangeListener) throws Exception {
        WatchCreateRequest createRequest = WatchCreateRequest.newBuilder()
            .setKey(ByteString.copyFrom(watchKey))
            .build();
        WatchRequest watchRequest = WatchRequest.newBuilder()
            .setNodeIdBytes(ByteString.copyFromUtf8(endpoint))
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

    public void unwatch(byte[] watchKey) throws Exception {
        WatchCancelRequest createRequest = WatchCancelRequest.newBuilder()
            .setKey(ByteString.copyFrom(watchKey))
            .build();
        WatchRequest watchRequest = WatchRequest.newBuilder()
            .setNodeIdBytes(ByteString.copyFromUtf8(endpoint))
            .setCancelRequest(createRequest)
            .build();
        watchRaftClient.watchClientRpc().unwatch(watchRequest);
    }

    @Override
    public void close() throws Exception {
        this.kvClient.close();
        this.watchRaftClient.close();
    }

}
