package org.dromara.hodor.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.service.HodorService;

/**
 * leader elect change listener
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class LeaderElectChangeListener implements DataChangeListener {

    private final HodorService hodorService;

    public LeaderElectChangeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        log.info("LeaderElect change event, {}", event);
        if (!SchedulerNode.isMasterActivePath(event.getPath())) {
            return;
        }
        if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            hodorService.electLeader();
        }
    }

}
