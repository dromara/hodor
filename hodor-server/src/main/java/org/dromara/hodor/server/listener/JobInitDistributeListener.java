package org.dromara.hodor.server.listener;

import java.util.List;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.HodorEventListener;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.service.HodorService;

/**
 * job distribute listener
 *
 * @author tomgs
 * @since 2020/7/30
 */
public class JobInitDistributeListener implements HodorEventListener<HodorMetadata> {

    private final CopySetManager copySetManager;

    private final HodorService hodorService;

    private final SchedulerManager schedulerManager;

    public JobInitDistributeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
        this.copySetManager = CopySetManager.getInstance();
        this.schedulerManager = SchedulerManager.getInstance();
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
            HodorScheduler activeScheduler = schedulerManager.createActiveScheduler(copySet.getServerId(), dataInterval);
            hodorService.addRunningJob(activeScheduler, dataInterval);
            // 备用节点数据
            List<String> servers = copySet.getServers();
            servers.forEach(server -> {
                // 排除主节点
                if (copySet.getLeader().equals(server)) {
                    return;
                }
                CopySet standbyCopySet = copySetManager.getCopySet(server);
                if (standbyCopySet == null) {
                    return;
                }
                DataInterval standbyDataInterval = standbyCopySet.getDataInterval();
                HodorScheduler standbyScheduler = schedulerManager.createStandbyScheduler(standbyCopySet.getServerId(), standbyDataInterval);
                hodorService.addRunningJob(standbyScheduler, standbyDataInterval);
            });
        });
    }

}
