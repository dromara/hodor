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
import java.util.List;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.dromara.hodor.common.raft.kv.exception.HodorKVClientException;
import org.dromara.hodor.common.raft.kv.exception.HodorKVConnectException;
import org.dromara.hodor.common.raft.kv.protocol.CmdType;
import org.dromara.hodor.common.raft.kv.protocol.ContainsKeyRequest;
import org.dromara.hodor.common.raft.kv.protocol.DeleteRequest;
import org.dromara.hodor.common.raft.kv.protocol.GetRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.dromara.hodor.common.raft.kv.protocol.PutRequest;
import org.dromara.hodor.common.raft.kv.protocol.ScanRequest;
import org.dromara.hodor.common.utils.ProtostuffUtils;

/**
 * HodorKVOperate
 *
 * @author tomgs
 * @since 1.0
 */
public class HodorKVOperator implements KVOperator {

    private final String clientId;

    private final String tableName;

    private final RaftClient raftClient;

    HodorKVOperator(String clientId, String tableName, final RaftClient raftClient) {
        this.clientId = clientId;
        this.tableName = tableName;
        this.raftClient = raftClient;
    }

    @Override
    public byte[] get(byte[] key) {
        GetRequest getRequest = GetRequest.builder()
            .key(key)
            .build();
        HodorKVRequest request = HodorKVRequest.builder()
            .table(tableName)
            .cmdType(CmdType.GET)
            .getRequest(getRequest)
            .build();
        HodorKVResponse response = handleReadRequest(request);
        if (response.getSuccess()) {
            return response.getGetResponse().getValue();
        } else {
            throw new HodorKVClientException(response.getMessage());
        }
    }

    @Override
    public void put(byte[] key, byte[] value) {
        PutRequest putRequest = PutRequest.builder()
            .key(key)
            .value(value)
            .build();
        HodorKVRequest request = HodorKVRequest.builder()
            .table(tableName)
            .cmdType(CmdType.PUT)
            .putRequest(putRequest)
            .build();
        HodorKVResponse response = handleWriteRequest(request);
        if (!response.getSuccess()) {
            throw new HodorKVClientException(response.getMessage());
        }
    }

    @Override
    public void putEphemeral(byte[] key, byte[] value) {
        PutRequest putRequest = PutRequest.builder()
            .key(key)
            .value(value)
            .build();
        HodorKVRequest request = HodorKVRequest.builder()
            .table(tableName)
            .sessionId(clientId)
            .cmdType(CmdType.PUT)
            .putRequest(putRequest)
            .build();
        HodorKVResponse response = handleWriteRequest(request);
        if (!response.getSuccess()) {
            throw new HodorKVClientException(response.getMessage());
        }
    }

    @Override
    public void delete(byte[] key) {
        DeleteRequest deleteRequest = DeleteRequest.builder()
            .key(key)
            .build();
        HodorKVRequest request = HodorKVRequest.builder()
            .table(tableName)
            .cmdType(CmdType.DELETE)
            .deleteRequest(deleteRequest)
            .build();
        HodorKVResponse response = handleWriteRequest(request);
        if (!response.getSuccess()) {
            throw new HodorKVClientException(response.getMessage());
        }
    }

    @Override
    public Boolean containsKey(byte[] key) {
        ContainsKeyRequest containsKeyRequest = ContainsKeyRequest.builder()
            .key(key)
            .build();
        HodorKVRequest request = HodorKVRequest.builder()
            .table(tableName)
            .cmdType(CmdType.CONTAINS_KEY)
            .containsKeyRequest(containsKeyRequest)
            .build();
        HodorKVResponse response = handleReadRequest(request);
        if (!response.getSuccess()) {
            throw new HodorKVClientException(response.getMessage());
        }
        return response.getContainsKeyResponse().getValue();
    }

    @Override
    public List<KVEntry> scan(byte[] startKey, byte[] endKey, boolean returnValue) {
        ScanRequest scanRequest = ScanRequest.builder()
            .startKey(startKey)
            .endKey(endKey)
            .build();
        HodorKVRequest request = HodorKVRequest.builder()
            .table(tableName)
            .cmdType(CmdType.SCAN)
            .scanRequest(scanRequest)
            .build();
        HodorKVResponse response = handleReadRequest(request);
        if (!response.getSuccess()) {
            throw new HodorKVClientException(response.getMessage());
        }
        return response.getScanResponse().getValue();
    }

    @Override
    public void close() throws IOException {
        raftClient.close();
    }

    private HodorKVResponse handleReadRequest(HodorKVRequest request) {
        try {
            final byte[] bytes = ProtostuffUtils.serialize(request);
            final RaftClientReply raftClientReply = raftClient.io().sendReadOnly(Message.valueOf(ByteString.copyFrom(bytes)));
            return ProtostuffUtils.deserialize(raftClientReply.getMessage().getContent().toByteArray(), HodorKVResponse.class);
        } catch (Exception e) {
            throw new HodorKVConnectException(e.getMessage(), e);
        }
    }

    private HodorKVResponse handleWriteRequest(HodorKVRequest request) {
        try {
            final byte[] bytes = ProtostuffUtils.serialize(request);
            final RaftClientReply raftClientReply = raftClient.io().send(Message.valueOf(ByteString.copyFrom(bytes)));
            return ProtostuffUtils.deserialize(raftClientReply.getMessage().getContent().toByteArray(), HodorKVResponse.class);
        } catch (Exception e) {
            throw new HodorKVConnectException(e.getMessage(), e);
        }
    }

}
