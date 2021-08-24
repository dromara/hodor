package org.dromara.hodor.server.listener;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.HodorEventListener;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;
import org.dromara.hodor.server.service.HodorService;

import java.util.List;

/**
 * job distribute listener
 *
 * @author tomgs
 * @since 2020/7/30
 */
@Slf4j
public class HodorSchedulerChangeListener implements HodorEventListener<List<CopySet>> {

    private final HodorService hodorService;

    private final SchedulerManager schedulerManager;

    public HodorSchedulerChangeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
        this.schedulerManager = SchedulerManager.getInstance();
    }

    @Override
    public void onEvent(final Event<List<CopySet>> event) {
        List<CopySet> copySetList = event.getValue();
        String serverEndpoint = hodorService.getServerEndpoint();
        if (CollectionUtil.isEmpty(copySetList)) {
            throw new HodorSchedulerException(StringUtils.format("not found copy set by endpoint {}.", serverEndpoint));
        }
        copySetList.forEach(copySet -> {
            // 主节点数据区间
            if (serverEndpoint.equals(copySet.getLeader())) {
                DataInterval activeDataInterval = copySet.getDataInterval();
                HodorScheduler scheduler = schedulerManager.getScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                if (scheduler != null && scheduler.getNumberOfJobs() > 0) {
                    // check is changed and switch to active
                    DataInterval schedulerDataInterval = schedulerManager.getSchedulerDataInterval(scheduler.getSchedulerName());
                    if (activeDataInterval.equals(schedulerDataInterval)) {
                        schedulerManager.addActiveScheduler(scheduler);
                        return;
                    }
                }
                // rebuild scheduler
                HodorScheduler activeScheduler = schedulerManager.createActiveSchedulerIfAbsent(serverEndpoint, copySet.getId(), activeDataInterval);
                activeScheduler.clear();
                hodorService.addRunningJob(activeScheduler, activeDataInterval);
                return;
            }
            // 备用节点数据
            if (copySet.getServers().contains(serverEndpoint)) {
                DataInterval standbyDataInterval = copySet.getDataInterval();
                HodorScheduler scheduler = schedulerManager.getScheduler(schedulerManager.createSchedulerName(serverEndpoint, copySet.getId()));
                if (scheduler != null && scheduler.getNumberOfJobs() > 0) {
                    // check is changed and switch to active
                    DataInterval schedulerDataInterval = schedulerManager.getSchedulerDataInterval(scheduler.getSchedulerName());
                    if (standbyDataInterval.equals(schedulerDataInterval)) {
                        schedulerManager.addStandByScheduler(scheduler);
                        return;
                    }
                }
                // rebuild scheduler
                HodorScheduler standbyScheduler = schedulerManager.createStandbySchedulerIfAbsent(serverEndpoint, copySet.getId(), standbyDataInterval);
                standbyScheduler.clear();
                hodorService.addRunningJob(standbyScheduler, standbyDataInterval);
            }
        });
    }

}
