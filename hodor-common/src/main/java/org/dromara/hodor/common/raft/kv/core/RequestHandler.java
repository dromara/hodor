/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.dromara.hodor.common.raft.kv.core;

import java.io.IOException;
import org.dromara.hodor.common.raft.kv.exception.HodorKVException;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVRequest;
import org.dromara.hodor.common.raft.kv.protocol.HodorKVResponse;

/**
 * Handler to handleRequest the HodorKVRequests.
 */
public interface RequestHandler {

    /**
     * Validates that the incoming OM request has required parameters.
     * TODO: Add more validation checks before writing the request to Ratis log.
     *
     * @param request client request to OM
     * @throws HodorKVException thrown if required parameters are set to null.
     */
    void validateRequest(final HodorKVRequest request) throws HodorKVException;

    /**
     * Handle the read requests, and returns HodorKVResponse.
     *
     * @param request read request
     * @return HodorKVResponse
     */
    HodorKVResponse handleReadRequest(final HodorKVRequest request) throws IOException;

    /**
     * Handle write requests. In HA this will be called from
     * OzoneManagerStateMachine applyTransaction method. In non-HA this will be
     * called from {@link null} for write
     * requests.
     *
     * @param request             - write request
     * @param transactionLogIndex - ratis transaction log index
     * @return OMClientResponse
     */
    HodorKVResponse handleWriteRequest(final HodorKVRequest request, final long transactionLogIndex) throws IOException;

    /**
     * Update the OzoneManagerDoubleBuffer. This will be called when
     * stateMachine is unpaused and set with new doublebuffer object.
     * @param ozoneManagerDoubleBuffer
     */
    //void updateDoubleBuffer(OzoneManagerDoubleBuffer ozoneManagerDoubleBuffer);

}
