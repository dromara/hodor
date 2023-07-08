package org.dromara.hodor.actuator.api.core;

import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.actuator.api.executor.ExecutorManager;
import org.dromara.hodor.common.executor.ExecutorInfo;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.node.NodeInfo;

/**
 * 节点管理
 *
 * @author tomgs
 * @since 1.0
 */
public class NodeManager {

    private final HodorProperties hodorProperties;

    private final ExecutorManager executorManager;

    public NodeManager(final HodorProperties hodorProperties, final ExecutorManager executorManager) {
        this.hodorProperties = hodorProperties;
        this.executorManager = executorManager;
    }

    public NodeInfo getNodeInfo() {
        String ip = HostUtils.getLocalIp();
        String hostName = HostUtils.getLocalHostName();
        Integer port = hodorProperties.getPort();
        long pid = Utils.Systems.getCurrentPID();
        Double cpuUsage = Utils.Systems.getCpuUsage();
        Double memUsage = Utils.Systems.getMemUsage();
        Double loadAverage = Utils.Systems.getLoadAverage();
        ExecutorInfo executorInfo = executorManager.getHodorExecutor().getExecutorInfo();

        return NodeInfo.builder()
            .ip(ip)
            .port(port)
            .hostname(hostName)
            .pid(pid)
            .cpuUsage(cpuUsage)
            .memoryUsage(memUsage)
            .loadAverage(loadAverage)
            .executeCount(executorInfo.getCompleteTaskCount())
            .queueSize(executorInfo.getQueueSize())
            .waitingQueueSize(executorInfo.getWaitTaskCount())
            //.version()
            .build();
    }

}
