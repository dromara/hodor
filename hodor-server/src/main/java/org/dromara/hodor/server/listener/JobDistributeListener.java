package org.dromara.hodor.server.listener;

import java.util.List;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.HodorEventListener;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.service.HodorService;

/**
 * job distribute listener
 *
 * @author tomgs
 * @since 2020/7/30
 */
public class JobDistributeListener implements HodorEventListener<HodorMetadata> {

    private final CopySetManager copySetManager;

    private final HodorService hodorService;

    public JobDistributeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
        this.copySetManager = CopySetManager.getInstance();
    }

    @Override
    public void onEvent(final Event<HodorMetadata> event) {
        final HodorMetadata metadata = event.getValue();
        List<CopySet> copySets = metadata.getCopySets();
        copySets.forEach(copySet -> {
            if (!hodorService.getServerEndpoint().equals(copySet.getLeader())) {
                return;
            }
            // 主节点数据区间
            DataInterval dataInterval = copySet.getDataInterval();
            hodorService.createActiveScheduler(copySet.getServerId(), dataInterval);
            // 备用节点数据
            List<String> servers = copySet.getServers();
            servers.forEach(server -> {
                // 排除主节点
                if (copySet.getLeader().equals(server)) {
                    return;
                }
                CopySet standbyCopySet = copySetManager.getCopySet(server);
                DataInterval standbyDataInterval = standbyCopySet.getDataInterval();
                hodorService.createStandbyScheduler(standbyCopySet.getLeader(), standbyDataInterval);
            });
        });
    }

}
