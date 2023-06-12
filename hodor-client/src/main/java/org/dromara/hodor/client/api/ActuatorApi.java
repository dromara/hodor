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

package org.dromara.hodor.client.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.dromara.hodor.client.model.LogQueryRequest;
import org.dromara.hodor.client.model.LogQueryResult;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.RemotingClient;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteLogResponse;

/**
 * ActuatorApi
 *
 * @author tomgs
 * @since 1.0
 */
public class ActuatorApi {

    private final RemotingMessageSerializer serializer;

    private final RemotingClient remotingClient;

    public ActuatorApi() {
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();;
        this.remotingClient = RemotingClient.getInstance();
    }

    public LogQueryResult queryLog(LogQueryRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        Host host = Host.of(request.getActuatorEndpoint());
        JobExecuteLogRequest executeLogRequest = new JobExecuteLogRequest();
        executeLogRequest.setRequestId(request.getRequestId());
        executeLogRequest.setGroupName(request.getGroupName());
        executeLogRequest.setJobName(request.getJobName());
        executeLogRequest.setOffset(request.getOffset());
        executeLogRequest.setLength(request.getLength());

        final byte[] bodyBytes = serializer.serialize(executeLogRequest);
        RemotingMessage requestMessage = RemotingMessage.builder()
            .header(buildJobLogHeader(bodyBytes.length, executeLogRequest))
            .body(bodyBytes)
            .build();
        final RemotingMessage remotingMessage = remotingClient.sendSyncRequest(host, requestMessage, 10);
        final JobExecuteLogResponse jobExecuteLogResponse = serializer.deserialize(remotingMessage.getBody(), JobExecuteLogResponse.class);
        LogQueryResult queryResult = new LogQueryResult();
        queryResult.setOffset(jobExecuteLogResponse.getOffset());
        queryResult.setLength(jobExecuteLogResponse.getLength());
        queryResult.setLogData(jobExecuteLogResponse.getData());
        return queryResult;
    }

    private Header buildJobLogHeader(int bodyLength, JobExecuteLogRequest request) {
        return Header.builder()
            .id(request.getRequestId())
            .version(RemotingConst.DEFAULT_VERSION)
            .type(MessageType.FETCH_JOB_LOG_REQUEST.getType())
            .length(bodyLength)
            .build();
    }

}
