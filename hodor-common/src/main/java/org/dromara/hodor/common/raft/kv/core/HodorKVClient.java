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

import java.io.IOException;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.RaftUtils;
import org.dromara.hodor.common.raft.kv.exception.HodorKVClientException;
import org.dromara.hodor.common.raft.kv.protocol.CmdType;
import org.dromara.hodor.common.raft.kv.protocol.DeleteRequest;
import org.dromara.hodor.common.raft.kv.protocol.GetRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;
import org.dromara.hodor.common.raft.kv.protocol.PutRequest;
import org.dromara.hodor.common.utils.ProtostuffUtils;

/**
 * HodorKVClient
 *
 * @author tomgs
 * @since 2022/4/6
 */
public class HodorKVClient implements KVClient {

    private final RaftClient raftClient;

    public HodorKVClient(String address) {
        HodorRaftGroup hodorRaftGroup = HodorRaftGroup.builder()
            .raftGroupName(KVConstant.HODOR_KV_GROUP_NAME)
            .addresses(address)
            .build();
        this.raftClient = RaftUtils.createClient(hodorRaftGroup.getRaftGroup());
    }

    public HodorKVClient(final HodorRaftGroup hodorRaftGroup) {
        this.raftClient = RaftUtils.createClient(hodorRaftGroup.getRaftGroup());
    }

    private HodorKVResponse handleRatisKVReadRequest(HodorKVRequest request) throws IOException {
        final byte[] bytes = ProtostuffUtils.serialize(request);
        final RaftClientReply raftClientReply = raftClient.io().sendReadOnly(Message.valueOf(ByteString.copyFrom(bytes)));
        return ProtostuffUtils.deserialize(raftClientReply.getMessage().getContent().toByteArray(), HodorKVResponse.class);
    }

    private HodorKVResponse handleRatisKVWriteRequest(HodorKVRequest request) throws IOException {
        final byte[] bytes = ProtostuffUtils.serialize(request);
        final RaftClientReply raftClientReply = raftClient.io().send(Message.valueOf(ByteString.copyFrom(bytes)));
        return ProtostuffUtils.deserialize(raftClientReply.getMessage().getContent().toByteArray(), HodorKVResponse.class);
    }

    @Override
    public byte[] get(byte[] key) {
        try {
            GetRequest getRequest = new GetRequest();
            getRequest.setKey(key);
            HodorKVRequest request = HodorKVRequest.builder()
                .cmdType(CmdType.GET)
                .getRequest(getRequest)
                .build();
            HodorKVResponse response = handleRatisKVReadRequest(request);
            if (response.getSuccess()) {
                return response.getGetResponse().getValue();
            } else {
                throw new HodorKVClientException(response.getMessage());
            }
        } catch (Exception e) {
            throw new HodorKVClientException(e.getMessage(), e);
        }
    }

    @Override
    public void put(byte[] key, byte[] value) {
        try {
            PutRequest putRequest = new PutRequest();
            putRequest.setKey(key);
            putRequest.setValue(value);
            HodorKVRequest request = HodorKVRequest.builder()
                .cmdType(CmdType.PUT)
                .putRequest(putRequest)
                .build();
            HodorKVResponse response = handleRatisKVWriteRequest(request);
            if (!response.getSuccess()) {
                throw new HodorKVClientException(response.getMessage());
            }
        } catch (Exception e) {
            throw new HodorKVClientException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(byte[] key) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest();
            deleteRequest.setKey(key);
            HodorKVRequest request = HodorKVRequest.builder()
                .cmdType(CmdType.DELETE)
                .deleteRequest(deleteRequest)
                .build();
            HodorKVResponse response = handleRatisKVWriteRequest(request);
            if (!response.getSuccess()) {
                throw new HodorKVClientException(response.getMessage());
            }
        } catch (Exception e) {
            throw new HodorKVClientException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        raftClient.close();
    }

}
