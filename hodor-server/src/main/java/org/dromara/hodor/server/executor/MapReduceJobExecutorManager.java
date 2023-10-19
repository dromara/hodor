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

package org.dromara.hodor.server.executor;

import cn.hutool.core.lang.TypeReference;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.IdGenerator;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.core.Constants;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.dispatch.JobDispatcher;
import org.dromara.hodor.server.executor.exception.JobScheduleException;
import org.dromara.hodor.server.executor.handler.HodorMapReduceJobRequestHandler;
import org.dromara.hodor.server.manager.ActuatorNodeManager;

/**
 * MapReduceJobExecutorManager
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class MapReduceJobExecutorManager {

    private static final MapReduceJobExecutorManager INSTANCE = new MapReduceJobExecutorManager();

    private final JobDispatcher dispatcher;

    private final AbstractAsyncEventPublisher<RemotingResponse<JobExecuteResponse>> responseEventPublisher;

    private final Map<JobKey, String> mapCommandMap;

    private final Map<JobKey, String> reduceCommandMap;

    private final Map<JobKey, HodorJobExecutionContext> commandContextMap;

    // instanceId --> <requestId --> result>
    //上游任务的结果 Map<requestId, result>
    private final Map<Long, Map<Long, Object>> mapJobExecuteResults;

    //上游任务的状态 Map<requestId, executeStatus>
    private final Map<Long, Map<Long, JobExecuteStatus>> mapJobExecuteStatuses;

    private final Map<Long, Integer> subJobCountMap;

    private MapReduceJobExecutorManager() {
        this.dispatcher = new JobDispatcher(new HodorMapReduceJobRequestHandler());
        this.responseEventPublisher = new AbstractAsyncEventPublisher<RemotingResponse<JobExecuteResponse>>() {
        };
        this.mapCommandMap = new ConcurrentHashMap<>();
        this.reduceCommandMap = new ConcurrentHashMap<>();
        this.commandContextMap = new ConcurrentHashMap<>();
        this.mapJobExecuteResults = new ConcurrentHashMap<>();
        this.mapJobExecuteStatuses = new ConcurrentHashMap<>();
        this.subJobCountMap = new ConcurrentHashMap<>();

        registerSplitStageListener();
        registerMapStageListener();
        registerReduceStageListener();
    }

    public static MapReduceJobExecutorManager getInstance() {
        return INSTANCE;
    }

    private void registerSplitStageListener() {
        responseEventPublisher.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            final JobExecuteResponse data = remotingResponse.getData();
            if (remotingResponse.isSuccess()) {
                if (!JobExecuteStatus.isSucceed(data.getStatus())) {
                    return;
                }
                final Long instanceId = data.getInstanceId();
                final byte[] result = data.getResult();
                final JobKey jobKey = data.getJobKey();
                if (result == null) {
                    throw new JobScheduleException("The job [{}] has no split data", jobKey);
                }

                final List<Host> hosts = ActuatorNodeManager.getInstance().getAvailableHosts(jobKey);
                if (hosts.isEmpty()) {
                    throw new JobScheduleException("The job [{}] has no available actuator nodes", jobKey);
                }
                final List<Object> splitList = SerializeUtils.deserialize(result, new TypeReference<List<Object>>() {
                }.getType());
                subJobCountMap.put(instanceId, splitList.size());

                final ConcurrentMap<Long, JobExecuteStatus> subJobMap = Maps.newConcurrentMap();
                for (int i = 0; i < splitList.size(); i++) {
                    // 分发子任务
                    HodorJobExecutionContext mapContext = getBaseContext(instanceId, jobKey);
                    mapContext.setParentJobData(splitList.get(i));
                    mapContext.setExecCommand(mapCommandMap.get(jobKey));
                    mapContext.setShardingCount(splitList.size());
                    mapContext.setShardingId(i);
                    Host selected = hosts.get(i % hosts.size());
                    mapContext.refreshHosts(selected);

                    // 记录map子任务
                    subJobMap.put(mapContext.getRequestId(), JobExecuteStatus.PENDING);

                    mapStageDispatch(mapContext);
                }
                mapJobExecuteStatuses.put(instanceId, subJobMap);
                mapJobExecuteResults.put(instanceId, Maps.newConcurrentMap());
            } else {
                final String errMsg = remotingResponse.getMsg();
                log.error("Job [{}] split stage execute failed, msg {}", data.getJobKey(), errMsg);
            }
        }, Constants.JobConstants.SPLIT_STAGE);
    }

    private HodorJobExecutionContext getBaseContext(Long instanceId, JobKey jobKey) {
        final HodorJobExecutionContext parentContext = commandContextMap.get(jobKey);
        if (parentContext == null) {
            throw new JobScheduleException("The job [{}] has finished.", jobKey);
        }
        final HodorJobExecutionContext subContext = Utils.Beans.copyProperties(parentContext, HodorJobExecutionContext.class);
        subContext.setInstanceId(instanceId);
        subContext.setRequestId(IdGenerator.defaultGenerator().nextId());
        return subContext;
    }

    private void registerMapStageListener() {
        // response
        responseEventPublisher.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            final JobExecuteResponse data = remotingResponse.getData();
            final JobKey jobKey = data.getJobKey();
            final Long instanceId = data.getInstanceId();
            if (remotingResponse.isSuccess()) {
                final Map<Long, JobExecuteStatus> mapJobExecuteStatusMap = mapJobExecuteStatuses.get(instanceId);
                final Map<Long, Object> mapJobExecuteResult = mapJobExecuteResults.get(instanceId);
                // Map stage
                if (JobExecuteStatus.isFinished(data.getStatus())) {
                    mapJobExecuteStatusMap.put(data.getRequestId(), data.getStatus());
                    mapJobExecuteResult.put(data.getRequestId(), StringUtils.decodeString(data.getResult()));
                }

                final Integer subMapJobSize = subJobCountMap.get(instanceId);
                if (mapJobExecuteStatusMap.size() < subMapJobSize) {
                    return;
                }

                // 检查子任务是否完成
                boolean allFinished = false;
                for (JobExecuteStatus status : mapJobExecuteStatusMap.values()) {
                    if (!JobExecuteStatus.isFinished(status)) {
                        allFinished = false;
                        break;
                    }
                    allFinished = true;
                }
                if (!allFinished) {
                    return;
                }

                if (reduceCommandMap.get(jobKey) == null) {
                    // 所有的都完成，但是没有reduce任务
                    commandContextMap.remove(jobKey);
                } else {
                    final List<Host> hosts = ActuatorNodeManager.getInstance().getAvailableHosts(jobKey);
                    if (hosts.isEmpty()) {
                        throw new JobScheduleException("The job [{}] has no available actuator nodes", jobKey);
                    }
                    HodorJobExecutionContext reduceContext = getBaseContext(instanceId, jobKey);
                    reduceContext.setExecCommand(reduceCommandMap.get(jobKey));
                    reduceContext.setParentJobExecuteStatuses(mapJobExecuteStatusMap);
                    reduceContext.setParentJobExecuteResults(mapJobExecuteResult);
                    reduceContext.setShardingId(-2);
                    reduceContext.resetHosts(hosts);

                    log.info("Job [{}] reduce stage dispatch", data.getJobKey());
                    reduceStageDispatch(reduceContext);

                    mapJobExecuteStatuses.remove(instanceId);
                    mapJobExecuteResults.remove(instanceId);
                }
            } else {
                final String errMsg = remotingResponse.getMsg();
                log.error("Job [{}] map stage execute failed, msg {}", data.getJobKey(), errMsg);
            }
        }, Constants.JobConstants.MAP_STAGE);
    }

    private void registerReduceStageListener() {
        responseEventPublisher.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            final JobExecuteResponse data = remotingResponse.getData();
            final JobKey jobKey = data.getJobKey();
            if (remotingResponse.isSuccess()) {
                // split stage
                if (JobExecuteStatus.isFinished(data.getStatus())) {
                    // 所有的都完成
                    commandContextMap.remove(jobKey);
                }
            } else {
                final String errMsg = remotingResponse.getMsg();
                log.error("Job [{}] map stage execute failed, msg {}", data.getJobKey(), errMsg);
            }
        }, Constants.JobConstants.REDUCE_STAGE);
    }

    public void splitStageDispatch(HodorJobExecutionContext context) {
        commandContextMap.put(context.getJobKey(), context);
        context.setStage(Constants.JobConstants.SPLIT_STAGE);
        dispatcher.dispatch(context);
    }

    public void mapStageDispatch(HodorJobExecutionContext context) {
        context.setStage(Constants.JobConstants.MAP_STAGE);
        dispatcher.dispatch(context);
    }

    public void reduceStageDispatch(HodorJobExecutionContext context) {
        context.setStage(Constants.JobConstants.REDUCE_STAGE);
        dispatcher.dispatch(context);
    }

    public void fireJobResponseHandler(String stage, RemotingResponse<JobExecuteResponse> remotingResponse) {
        responseEventPublisher.parallelPublish(Event.create(remotingResponse, stage));
    }

    public void putMapCommand(JobKey jobKey, String mapCommand) {
        mapCommandMap.put(jobKey, mapCommand);
    }

    public void putReduceCommand(JobKey jobKey, String reduceCommand) {
        reduceCommandMap.put(jobKey, reduceCommand);
    }
}
