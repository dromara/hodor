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

package org.dromara.hodor.admin.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.domain.SchedulerNodeInfo;
import org.dromara.hodor.admin.service.ScheduleOperatorService;
import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.api.SchedulerApi;
import org.dromara.hodor.client.model.SchedulerNodeResult;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.springframework.stereotype.Service;

/**
 * ScheduleOperatorServiceImpl
 *
 * @author tomgs
 * @since 1.0
 */
@Service
@Slf4j
public class ScheduleOperatorServiceImpl implements ScheduleOperatorService {

    private final SchedulerApi schedulerApi;

    public ScheduleOperatorServiceImpl(final HodorApiClient hodorApiClient) {
        this.schedulerApi = hodorApiClient.createApi(SchedulerApi.class);
    }

    @Override
    public List<SchedulerNodeInfo> getSchedulers() throws Exception {
        List<SchedulerNodeInfo> schedulerNodeInfos = new ArrayList<>();
        final List<SchedulerNodeResult> schedulers = schedulerApi.getSchedulers();
        for (SchedulerNodeResult result : schedulers) {
            final NodeInfo nodeInfo = result.getNodeInfo();
            SchedulerNodeInfo schedulerNodeInfo = new SchedulerNodeInfo();
            schedulerNodeInfo.setName(result.getName())
                .setReportTime(result.getReportTime())
                .setRole(result.getRole())
                .setHostname(nodeInfo.getHostname())
                .setPort(nodeInfo.getPort())
                .setPid(nodeInfo.getPid())
                .setVersion(nodeInfo.getVersion())
                .setCpuUsage(nodeInfo.getCpuUsage())
                .setLoadAverage(nodeInfo.getLoadAverage())
                .setMemoryUsage(nodeInfo.getMemoryUsage())
                .setIp(nodeInfo.getIp());
            schedulerNodeInfos.add(schedulerNodeInfo);
        }
        return schedulerNodeInfos;
    }

    @Override
    public HodorMetadata getMetadata(String endpoint) throws Exception {
        HodorMetadata metadata = schedulerApi.getMetadata(endpoint);
        return metadata;
    }

}
