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

import cn.hutool.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.exception.HodorClientException;
import org.dromara.hodor.client.model.LogQueryRequest;
import org.dromara.hodor.client.model.LogQueryResult;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.connect.ConnectStringParser;
import org.dromara.hodor.common.connect.TrySender;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.actuator.BindingInfo;
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
@Slf4j
public class ActuatorApi {

    private final GsonUtils gsonUtils = GsonUtils.getInstance();

    private final ConnectStringParser connectStringParser;

    private final String appName;

    private final String appKey;

    private final RemotingMessageSerializer serializer;

    private final RemotingClient remotingClient;

    public ActuatorApi(ConnectStringParser connectStringParser, String appName, String appKey) {
        this.connectStringParser = connectStringParser;
        this.appName = appName;
        this.appKey = appKey;
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        this.remotingClient = RemotingClient.getInstance();
    }

    public LogQueryResult queryLog(LogQueryRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        final int timeout = request.getTimeout() > 0 ? request.getTimeout() : 3;
        Host host = Host.of(request.getActuatorEndpoint());
        JobExecuteLogRequest executeLogRequest = new JobExecuteLogRequest();
        executeLogRequest.setRequestId(request.getRequestId());
        executeLogRequest.setGroupName(request.getGroupName());
        executeLogRequest.setJobName(request.getJobName());
        executeLogRequest.setOffset(request.getOffset());
        executeLogRequest.setLength(request.getLength());

        final JobExecuteLogResponse jobExecuteLogResponse = getJobExecuteLogResponse(host, executeLogRequest, timeout);
        LogQueryResult queryResult = new LogQueryResult();
        queryResult.setOffset(jobExecuteLogResponse.getOffset());
        queryResult.setLength(jobExecuteLogResponse.getLength());
        queryResult.setLogData(jobExecuteLogResponse.getData());
        return queryResult;
    }

    private JobExecuteLogResponse getJobExecuteLogResponse(Host host, JobExecuteLogRequest executeLogRequest, int timeout) throws InterruptedException, ExecutionException, TimeoutException {
        final byte[] bodyBytes = serializer.serialize(executeLogRequest);
        RemotingMessage requestMessage = RemotingMessage.builder()
            .header(buildJobLogHeader(bodyBytes.length, executeLogRequest))
            .body(bodyBytes)
            .build();
        final RemotingMessage remotingMessage = remotingClient.sendSyncRequest(host, requestMessage, timeout);
        return serializer.deserialize(remotingMessage.getBody(), JobExecuteLogResponse.class);
    }

    private Header buildJobLogHeader(int bodyLength, JobExecuteLogRequest request) {
        return Header.builder()
            .id(request.getRequestId())
            .version(RemotingConst.DEFAULT_VERSION)
            .type(MessageType.FETCH_JOB_LOG_REQUEST.getType())
            .length(bodyLength)
            .build();
    }

    public void binding(String clusterName, String group) throws Exception {
        BindingInfo bindingInfo = new BindingInfo()
            .setClusterName(clusterName)
            .setGroupName(group);
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Utils.Https.createPost(url + "/actuator/binding")
            .body(gsonUtils.toJson(bindingInfo))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute());
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Binding failure, " + response.body());
        }
        log.debug("Binding result: {}", response.body());
    }

    public void unbinding(String clusterName, String group) throws Exception {
        BindingInfo bindingInfo = new BindingInfo()
            .setClusterName(clusterName)
            .setGroupName(group);
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Utils.Https.createPost(url + "/actuator/unbinding")
            .body(gsonUtils.toJson(bindingInfo))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute());
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Unbinding failure, " + response.body());
        }
        log.debug("Unbinding result: {}", response.body());
    }
}
