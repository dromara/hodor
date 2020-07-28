package org.dromara.hodor.server.service;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.server.component.Constants;
import org.dromara.hodor.server.component.LifecycleComponent;
import org.dromara.hodor.server.listener.LeaderElectChangeListener;
import org.dromara.hodor.server.listener.MetadataChangeListener;
import org.springframework.stereotype.Service;

/**
 * hodor service
 *
 * @author tomgs
 * @since 2020/6/29
 */
@Slf4j
@Service
public class HodorService implements LifecycleComponent {

    private final LeaderService leaderService;

    private final RegisterService registerService;

    public HodorService(final LeaderService leaderService, final RegisterService registerService) {
        this.leaderService = leaderService;
        this.registerService = registerService;
    }

    @Override
    public void start() {
        Integer currRunningNodeCount = registerService.getRunningNodeCount();
        while (currRunningNodeCount < Constants.LEAST_NODE_COUNT) {
            ThreadUtils.sleep(TimeUnit.MILLISECONDS, 1000);
            currRunningNodeCount = registerService.getRunningNodeCount();
        }
        //init data
        registerService.registryMetadataListener(new MetadataChangeListener());
        registerService.registryElectLeaderListener(new LeaderElectChangeListener(leaderService));
        //select leader
        leaderService.electLeader();
    }

    @Override
    public void stop() {
        registerService.stop();
        leaderService.stop();
    }

}
