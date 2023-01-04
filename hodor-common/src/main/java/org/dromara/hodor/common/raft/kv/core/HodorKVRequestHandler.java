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

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dromara.hodor.common.raft.kv.exception.HodorKVException;
import org.dromara.hodor.common.raft.kv.protocol.CmdType;
import org.dromara.hodor.common.raft.kv.protocol.ContainsKeyRequest;
import org.dromara.hodor.common.raft.kv.protocol.ContainsKeyResponse;
import org.dromara.hodor.common.raft.kv.protocol.DeleteRequest;
import org.dromara.hodor.common.raft.kv.protocol.GetRequest;
import org.dromara.hodor.common.raft.kv.protocol.GetResponse;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.dromara.hodor.common.raft.kv.protocol.PutRequest;
import org.dromara.hodor.common.raft.kv.protocol.ScanRequest;
import org.dromara.hodor.common.raft.kv.protocol.ScanResponse;
import org.dromara.hodor.common.raft.kv.storage.DBStore;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;
import org.dromara.hodor.common.utils.BytesUtil;

/**
 * HodorKVRequestHandler
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorKVRequestHandler implements RequestHandler {

    private final DBStore dbStore;

    public HodorKVRequestHandler(final StorageEngine storageEngine) {
        this.dbStore = storageEngine.getRawDBStore();
    }

    @Override
    public void validateRequest(HodorKVRequest request) throws HodorKVException {
        CmdType cmdType = request.getCmdType();
        if (cmdType == null) {
            throw new HodorKVException("CmdType is null",
                HodorKVException.ResultCodes.INVALID_REQUEST);
        }
        //if (request.getClientId() == null) {
        //    throw new HodorKVException("ClientId is null",
        //        HodorKVException.ResultCodes.INVALID_REQUEST);
        //}

        //// Layout version should have been set up the leader while serializing
        //// the request, and hence cannot be null. This version is used by each
        //// node to identify which request handler version to use.
        //if (request.getLayoutVersion() == null) {
        //    throw new HodorKVException("LayoutVersion for request is null.",
        //        HodorKVException.ResultCodes.INTERNAL_ERROR);
        //}
    }

    @Override
    public HodorKVResponse handleReadRequest(HodorKVRequest kvRequest) {
        final CmdType cmdType = kvRequest.getCmdType();
        HodorKVResponse.HodorKVResponseBuilder builder = createHodorKVResponseBuilder(kvRequest, cmdType);
        switch (cmdType) {
            case GET:
                log.info("GET op.");
                final GetRequest getRequest = kvRequest.getGetRequest();
                byte[] result = dbStore.get(getRequest.getKey());
                GetResponse getResponse = GetResponse.builder()
                    .value(result)
                    .build();
                builder.getResponse(getResponse);
                break;
            case CONTAINS_KEY:
                log.info("CONTAINS_KEY op.");
                final ContainsKeyRequest containsKeyRequest = kvRequest.getContainsKeyRequest();
                try {
                    final Boolean exists = dbStore.containsKey(containsKeyRequest.getKey());
                    ContainsKeyResponse containsKeyResponse = ContainsKeyResponse.builder()
                        .value(exists)
                        .build();
                    builder.containsKeyResponse(containsKeyResponse);
                } catch (Exception e) {
                    log.error("DELETE exception: {}", e.getMessage(), e);
                    builder.success(false)
                        .message(e.getMessage());
                }
                break;
            case SCAN:
                log.info("SCAN op.");
                final ScanRequest scanRequest = kvRequest.getScanRequest();
                final byte[] startKey = scanRequest.getStartKey();
                final byte[] endKey = scanRequest.getEndKey();
                try {
                    final List<KVEntry> kvEntries = dbStore.scan(startKey, endKey, scanRequest.isReturnValue());
                    ScanResponse scanResponse = ScanResponse.builder()
                        .value(kvEntries)
                        .build();
                    builder.scanResponse(scanResponse);
                } catch (Exception e) {
                    log.error("Fail to [SCAN], range: ['[{}, {})'], {}.", BytesUtil.toHex(startKey), BytesUtil.toHex(endKey),
                        ExceptionUtils.getStackTrace(e));
                    builder.success(false)
                        .message(e.getMessage());
                }
                break;
            default:
                throw new RuntimeException("Unsupported query request type: " + cmdType);
        }
        return builder.build();
    }

    @Override
    public HodorKVResponse handleWriteRequest(HodorKVRequest kvRequest, long transactionLogIndex) {
        final CmdType cmdType = kvRequest.getCmdType();
        HodorKVResponse.HodorKVResponseBuilder builder = createHodorKVResponseBuilder(kvRequest, cmdType);
        switch (cmdType) {
            case PUT:
                log.info("PUT op.");
                final PutRequest putRequest = kvRequest.getPutRequest();
                try {
                    dbStore.put(putRequest.getKey(), putRequest.getValue());
                } catch (Exception e) {
                    log.error("PUT exception: {}", e.getMessage(), e);
                    builder.success(false)
                        .message(e.getMessage());
                }
                break;
            case DELETE:
                log.info("DELETE op.");
                final DeleteRequest deleteRequest = kvRequest.getDeleteRequest();
                try {
                    dbStore.delete(deleteRequest.getKey());
                } catch (Exception e) {
                    log.error("DELETE exception: {}", e.getMessage(), e);
                    builder.success(false)
                        .message(e.getMessage());
                }
                break;
            default:
                throw new RuntimeException("Unsupported write request type: " + cmdType);
        }
        return builder.build();
    }

    public static HodorKVResponse.HodorKVResponseBuilder createHodorKVResponseBuilder(HodorKVRequest kvRequest, CmdType cmdType) {
        return HodorKVResponse.builder()
            .requestId(kvRequest.getRequestId())
            .traceId(kvRequest.getTraceId())
            .cmdType(cmdType)
            .success(true);
    }

}
