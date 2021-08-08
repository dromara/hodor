package org.dromara.hodor.server.listener;

import cn.hutool.core.collection.CollectionUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.HodorEventListener;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.service.HodorService;

/**
 * job distribute listener
 *
 * @author tomgs
 * @since 2020/7/30
 */
@Slf4j
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
        String serverEndpoint = hodorService.getServerEndpoint();
        List<CopySet> copySetList = copySetManager.getCopySet(serverEndpoint);
        if (CollectionUtil.isEmpty(copySetList)) {
            throw new HodorSchedulerException(StringUtils.format("not found copy set by endpoint {}.", serverEndpoint));
        }
        copySetList.forEach(copySet -> {
            // 主节点数据区间
            if (serverEndpoint.equals(copySet.getLeader())) {
                DataInterval dataInterval = copySet.getDataInterval();
                HodorScheduler activeScheduler = schedulerManager.createActiveScheduler(copySet.getServerId(), dataInterval);
                activeScheduler.clear();
                hodorService.addRunningJob(activeScheduler, dataInterval);
                return;
            }
            // 备用节点数据
            if (copySet.getServers().contains(serverEndpoint)) {
                DataInterval standbyDataInterval = copySet.getDataInterval();
                HodorScheduler standbyScheduler = schedulerManager.createStandbyScheduler(copySet.getServerId(), standbyDataInterval);
                standbyScheduler.clear();
                hodorService.addRunningJob(standbyScheduler, standbyDataInterval);
            }
        });
    }

}