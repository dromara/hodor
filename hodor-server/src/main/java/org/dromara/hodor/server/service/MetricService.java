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

package org.dromara.hodor.server.service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.HodorLifecycle;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.common.utils.TimeUtil;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.model.scheduler.SchedulerInfo;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.config.HodorServerProperties;
import org.springframework.stereotype.Service;

/**
 * MetricService
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service
public class MetricService implements HodorLifecycle {

    private final HodorServerProperties properties;

    private final LeaderService leaderService;

    private final RegistryService registryService;

    private final ScheduledThreadPoolExecutor reportSender;

    private final String version = "1.0.0";

    public MetricService(final HodorServerProperties properties,
                         final LeaderService leaderService,
                         final RegistryService registryService) {
        this.properties = properties;
        this.leaderService = leaderService;
        this.registryService = registryService;
        this.reportSender = new ScheduledThreadPoolExecutor(1,
            HodorThreadFactory.create("hodor-metrics-sender", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Override
    public void start() {
        reportSender.scheduleWithFixedDelay(() -> {
            try {
                final SchedulerInfo serverInfo = getServerInfo();
                final String reportInfo = Utils.Jsons.toJson(serverInfo);
                log.info("Report server node info, {}", reportInfo);
                registryService.createServerNode(SchedulerNode.getServerMetricsNodePath(registryService.getServerEndpoint()), reportInfo);
            } catch (Throwable e) {
                log.warn("Get server info error, {}", e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        reportSender.shutdown();
    }

    public SchedulerInfo getServerInfo() {
        SchedulerInfo schedulerInfo = new SchedulerInfo();
        schedulerInfo.setName(properties.getClusterName())
            .setRole(leaderService.isLeader() ? "LEADER" : "FOLLOWER")
            .setReportTime(TimeUtil.currentTimeMillis())
            .setNodeInfo(getNodeInfo());
        return schedulerInfo;
    }

    public NodeInfo getNodeInfo() {
        String ip = HostUtils.getLocalIp();
        String hostName = HostUtils.getLocalHostName();
        Integer port = properties.getNetServerPort();
        long pid = Utils.Systems.getCurrentPID();
        Double cpuUsage = Utils.Systems.getCpuUsage();
        Double memUsage = Utils.Systems.getMemUsage();
        Double loadAverage = Utils.Systems.getLoadAverage();

        return NodeInfo.builder()
            .ip(ip)
            .port(port)
            .hostname(hostName)
            .pid(pid)
            .cpuUsage(cpuUsage)
            .memoryUsage(memUsage)
            .loadAverage(loadAverage)
            .version(version)
            .build();
    }
}
