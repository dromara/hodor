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
 * @since 1.0
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
            log.info("server {} to be leader.", registryService.getServerEndpoint());
            callback.execute();
        });
    }


    /**
     * 当期节点是否为主节点
     */
    public boolean isLeader() {
        return registryCenter.isLeaderNode();
    }

}
