package org.dromara.hodor.server.service;

import org.dromara.hodor.common.utils.LocalHost;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.node.LeaderNode;
import org.springframework.stereotype.Service;

/**
 * leader service
 *
 * @author tomgs
 * @since 2020/6/30
 */
@Service
public class LeaderService {

    private final RegistryCenter registryCenter;

    public LeaderService(final RegisterService registerService) {
        this.registryCenter = registerService.getRegistryCenter();
    }

    /**
     * 选举主节点
     */
    public void electLeader(final LeaderExecutionCallback callback) {
        registryCenter.executeInLeader(LeaderNode.LATCH_PATH, () -> {
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
        registryCenter.createEphemeral(LeaderNode.INSTANCE_PATH, LocalHost.getIp());
    }

    /**
     *是否存在主节点
     */
    public boolean hasLeader() {
        return registryCenter.checkExists(LeaderNode.INSTANCE_PATH);
    }

    /**
     * 当期节点是否为主节点
     */
    public boolean isLeader() {
        return !LocalHost.getIp().equals(registryCenter.get(LeaderNode.INSTANCE_PATH));
    }

}
