package org.dromara.hodor.client.core;

import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.config.HodorProperties;
import org.dromara.hodor.client.executor.ExecutorManager;
import org.dromara.hodor.common.executor.ExecutorInfo;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.common.utils.MachineUtils;
import org.dromara.hodor.model.node.NodeInfo;

/**
 * 节点管理
 *
 * @author tomgs
 * @date 2021/01/18
 */
public class NodeManager {

    private static final NodeManager INSTANCE = new NodeManager();

    private final HodorProperties hodorProperties;

    private final ExecutorManager executorManager;

    private NodeManager() {
        this.hodorProperties = ServiceProvider.getInstance().getBean(HodorProperties.class);
        this.executorManager = ExecutorManager.getInstance();
    }

    public static NodeManager getInstance() {
        return INSTANCE;
    }

    public NodeInfo getNodeInfo() {
        String ip = HostUtils.getLocalIp();
        String hostName = HostUtils.getLocalHostName();
        Integer port = hodorProperties.getPort();
        String pid = MachineUtils.getPID();
        Double cpuUsage = MachineUtils.getCpuUsage();
        Double memUsage = MachineUtils.getMemUsage();
        //Double loadAverage = MachineUtils.getLoadAverage();
        ExecutorInfo executorInfo = executorManager.getHodorExecutor().getExecutorInfo();

        return NodeInfo.builder()
            .ip(ip)
            .port(port)
            .hostname(hostName)
            .pid(pid)
            .cpuRatio(cpuUsage)
            .memoryRatio(memUsage)
            //.loadAverageRatio()
            .executeCount(executorInfo.getCompleteTaskCount())
            .queueSize(executorInfo.getQueueSize())
            .waitingQueueSize(executorInfo.getWaitTaskCount())
            //.version()
            .build();
    }

}
