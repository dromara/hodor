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

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.exception.HodorClientException;
import org.dromara.hodor.client.model.KillJobRequest;
import org.dromara.hodor.client.model.KillJobResult;
import org.dromara.hodor.client.model.LogQueryRequest;
import org.dromara.hodor.client.model.LogQueryResult;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.connect.ConnectStringParser;
import org.dromara.hodor.common.connect.TrySender;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.Utils.Https;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.actuator.BindingInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.remoting.api.RemotingClient;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteLogResponse;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;

/**
 * ActuatorApi
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class ActuatorApi {

    private final ConnectStringParser connectStringParser;

    private final String appName;

    private final String appKey;

    private final RemotingMessageSerializer serializer;

    private final RemotingClient remotingClient;

    private final TypeReference<RemotingResponse<JobExecuteLogResponse>> logResponseTypeReference;
    private final TypeReference<RemotingResponse<KillRunningJobResponse>> killResponseTypeReference;

    public ActuatorApi(ConnectStringParser connectStringParser, String appName, String appKey) {
        this.connectStringParser = connectStringParser;
        this.appName = appName;
        this.appKey = appKey;
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
        this.remotingClient = RemotingClient.getInstance();
        this.logResponseTypeReference = new TypeReference<RemotingResponse<JobExecuteLogResponse>>() {};
        this.killResponseTypeReference = new TypeReference<RemotingResponse<KillRunningJobResponse>>() {};
    }

    public LogQueryResult queryLog(LogQueryRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        final int timeout = request.getTimeout() > 0 ? request.getTimeout() : 3000;
        Host host = Host.of(request.getActuatorEndpoint());
        JobExecuteLogRequest executeLogRequest = new JobExecuteLogRequest();
        executeLogRequest.setRequestId(request.getRequestId());
        executeLogRequest.setGroupName(request.getGroupName());
        executeLogRequest.setJobName(request.getJobName());
        executeLogRequest.setOffset(request.getOffset());
        executeLogRequest.setLength(request.getLength());

        final RemotingResponse<JobExecuteLogResponse> jobExecuteLogResponse = getJobExecuteLogResponse(host, executeLogRequest, timeout);
        if (!jobExecuteLogResponse.isSuccess()) {
            throw new HodorClientException("QueryLog failure, " + jobExecuteLogResponse.getMsg());
        }
        final JobExecuteLogResponse logResponseData = jobExecuteLogResponse.getData();
        LogQueryResult queryResult = new LogQueryResult();
        queryResult.setOffset(logResponseData.getOffset());
        queryResult.setLength(logResponseData.getLength());
        queryResult.setLogData(logResponseData.getData());
        return queryResult;
    }

    private RemotingResponse<JobExecuteLogResponse> getJobExecuteLogResponse(Host host, JobExecuteLogRequest executeLogRequest, int timeout) throws InterruptedException, ExecutionException, TimeoutException {
        final byte[] bodyBytes = serializer.serialize(executeLogRequest);
        RemotingMessage requestMessage = RemotingMessage.builder()
            .header(buildJobLogHeader(bodyBytes.length, executeLogRequest))
            .body(bodyBytes)
            .build();
        final RemotingMessage remotingMessage = remotingClient.sendSyncRequest(host, requestMessage, timeout);
        return serializer.deserialize(remotingMessage.getBody(), logResponseTypeReference.getType());
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
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/actuator/binding")
                .body(Jsons.toJson(bindingInfo))
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("ActuatorApi binding execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Binding failure, " + response.body());
        }
        log.debug("Binding result: {}", response.body());
    }

    public void unbinding(String clusterName, String group) throws Exception {
        BindingInfo bindingInfo = new BindingInfo()
            .setClusterName(clusterName)
            .setGroupName(group);
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/actuator/unbinding")
                .body(Jsons.toJson(bindingInfo))
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("ActuatorApi unbinding execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Unbinding failure, " + response.body());
        }
        log.debug("Unbinding result: {}", response.body());
    }

    public List<BindingInfo> listBinding() throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/actuator/listBinding")
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("ActuatorApi listBinding execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Unbinding failure, " + response.body());
        }
        log.debug("ListBinding result: {}", response.body());
        final HodorResult<List<BindingInfo>> hodorResult = Jsons.toBean(response.body(),
            new TypeReference<HodorResult<List<BindingInfo>>>() {});
        if (!hodorResult.isSuccess()) {
            throw new HodorClientException(hodorResult.getMsg());
        }
        return hodorResult.getData();
    }

    public List<ActuatorInfo> actuatorInfos() throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/actuator/actuatorInfos")
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("ActuatorApi actuatorInfos execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Get actuatorInfos failure, " + response.body());
        }
        log.debug("ListBinding result: {}", response.body());
        final HodorResult<List<ActuatorInfo>> hodorResult = Jsons.toBean(response.body(),
            new TypeReference<HodorResult<List<ActuatorInfo>>>() {});
        if (!hodorResult.isSuccess()) {
            throw new HodorClientException(hodorResult.getMsg());
        }
        return hodorResult.getData();
    }

    public List<String> allClusters() throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/actuator/allClusters")
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("ActuatorApi allClusters execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("ListBinding failure, " + response.body());
        }
        log.debug("ListBinding result: {}", response.body());
        final HodorResult<List<String>> hodorResult = Jsons.toBean(response.body(),
            new TypeReference<HodorResult<List<String>>>() {});
        if (!hodorResult.isSuccess()) {
            throw new HodorClientException(hodorResult.getMsg());
        }
        return hodorResult.getData();
    }

    public KillJobResult killRunningJob(KillJobRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        final int timeout = request.getTimeout() > 0 ? request.getTimeout() : 3000;
        Host host = Host.of(request.getActuatorEndpoint());
        KillRunningJobRequest killRunningJobRequest = new KillRunningJobRequest();
        killRunningJobRequest.setRequestId(request.getRequestId());
        final RemotingResponse<KillRunningJobResponse> killRunningJobResponse = getKillRunningJobResponse(host, killRunningJobRequest, timeout);
        if (!killRunningJobResponse.isSuccess()) {
            throw new HodorClientException("Kill Job Failure, " + killRunningJobResponse.getMsg());
        }
        final KillRunningJobResponse killResponseData = killRunningJobResponse.getData();
        KillJobResult killJobResult = new KillJobResult();
        killJobResult.setStatus(killResponseData.getStatus());
        killJobResult.setCompleteTime(killResponseData.getCompleteTime());
        return killJobResult;
    }

    private RemotingResponse<KillRunningJobResponse> getKillRunningJobResponse(Host host, KillRunningJobRequest killRunningJobRequest, int timeout) throws InterruptedException, ExecutionException, TimeoutException {
        final byte[] bodyBytes = serializer.serialize(killRunningJobRequest);
        RemotingMessage requestMessage = RemotingMessage.builder()
            .header(buildKillRunningJobHeader(bodyBytes.length, killRunningJobRequest))  // 创建请求头
            .body(bodyBytes)  // 请求体
            .build();
        final RemotingMessage remotingMessage = remotingClient.sendSyncRequest(host, requestMessage, timeout);
        return serializer.deserialize(remotingMessage.getBody(), killResponseTypeReference.getType());
    }

    private Header buildKillRunningJobHeader(int bodyLength, KillRunningJobRequest request) {
        return Header.builder()
            .id(request.getRequestId())
            .version(RemotingConst.DEFAULT_VERSION)
            .type(MessageType.KILL_JOB_REQUEST.getType())
            .length(bodyLength)
            .build();
    }
}
