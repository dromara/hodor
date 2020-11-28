package org.dromara.hodor.server.listener;

import java.util.List;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.ObjectListener;
import org.dromara.hodor.core.CopySet;
import org.dromara.hodor.core.HodorMetadata;
import org.dromara.hodor.core.manager.CopySetManager;
import org.dromara.hodor.server.service.HodorService;

/**
 * job distribute listener
 *
 * @author tomgs
 * @since 2020/7/30
 */
public class JobDistributeListener implements ObjectListener<HodorMetadata> {

    private final CopySetManager copySetManager;

    private final HodorService hodorService;

    public JobDistributeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
        this.copySetManager = CopySetManager.getInstance();
    }

    @Override
    public void onEvent(Event<HodorMetadata> event) {
        final HodorMetadata metadata = event.getValue();
        List<CopySet> copySets = metadata.getCopySets();
        copySets.forEach(e -> {
            if (!hodorService.getServerId().equals(e.getLeader())) {
                return;
            }
            // 主节点数据区间
            List<Long> dataInterval = e.getDataInterval();
            hodorService.createActiveScheduler(e.getLeader(), dataInterval);
            // 备用节点数据
            List<String> servers = e.getServers();
            servers.forEach(server -> {
                // 排除主节点
                if (e.getLeader().equals(server)) {
                    return;
                }
                CopySet standbyCopySet = copySetManager.getCopySet(server);
                List<Long> standbyDataInterval = standbyCopySet.getDataInterval();
                hodorService.createStandbyScheduler(standbyCopySet.getLeader(), standbyDataInterval);
            });
        });
    }

}
