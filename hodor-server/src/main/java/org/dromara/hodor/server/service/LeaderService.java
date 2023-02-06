package org.dromara.hodor.server.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.springframework.stereotype.Service;

/**
 * leader service
 *
 * @author tomgs
 * @since 2020/6/30
 */
@Service
@Slf4j
public class LeaderService {

    private final RegistryCenter registryCenter;

    private final RegistryService registryService;

    public LeaderService(final RegistryService registryService) {
        this.registryService = registryService;
        this.registryCenter = registryService.getRegistryCenter();
    }

    /**
     * 选举主节点
     */
    public void electLeader(final LeaderExecutionCallback callback) {
        registryCenter.executeInLeader(SchedulerNode.LATCH_PATH, () -> {
            createLeaderNode();
            callback.execute();
            /*if (!hasLeader()) {
                createLeaderNode();
                callback.execute();
            }*/
        });
    }

    /**
     * 创建主节点
     */
    public void createLeaderNode() {
        registryCenter.createEphemeral(SchedulerNode.MASTER_ACTIVE_PATH, registryService.getServerEndpoint());
    }

    /**
     *是否存在主节点
     */
    public boolean hasLeader() {
        return registryCenter.checkExists(SchedulerNode.MASTER_ACTIVE_PATH);
    }

    /**
     * 当期节点是否为主节点
     */
    public boolean isLeader() {
        return registryService.getServerEndpoint().equals(getLeaderEndpoint());
    }

    public String getLeaderEndpoint() {
        return registryCenter.get(SchedulerNode.MASTER_ACTIVE_PATH);
    }

}
