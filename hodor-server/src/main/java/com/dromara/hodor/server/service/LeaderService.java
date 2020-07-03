package com.dromara.hodor.server.service;

import org.dromara.hodor.common.utils.LocalHost;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.node.LeaderNode;

/**
 * leader service
 *
 * @author tomgs
 * @since 2020/6/30
 */
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

}
