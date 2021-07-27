package org.dromara.hodor.server.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.node.ServerNode;
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

    private final RegisterService registerService;

    public LeaderService(final RegisterService registerService) {
        this.registerService = registerService;
        this.registryCenter = registerService.getRegistryCenter();
    }

    /**
     * 选举主节点
     */
    public void electLeader(final LeaderExecutionCallback callback) {
        registryCenter.executeInLeader(ServerNode.LATCH_PATH, () -> {
            if (!hasLeader()) {
                createLeaderNode();
                callback.execute();
            }
        });
    }

    /**
     * 创建主节点
     */
    public void createLeaderNode() {
        registryCenter.createEphemeral(ServerNode.ACTIVE_PATH, registerService.getServerId());
    }

    /**
     *是否存在主节点
     */
    public boolean hasLeader() {
        return registryCenter.checkExists(ServerNode.ACTIVE_PATH);
    }

    /**
     * 当期节点是否为主节点
     */
    public boolean isLeader() {
        return !registerService.getServerId().equals(getLeaderEndpoint());
    }

    public String getLeaderEndpoint() {
        return registryCenter.get(ServerNode.ACTIVE_PATH);
    }

}
