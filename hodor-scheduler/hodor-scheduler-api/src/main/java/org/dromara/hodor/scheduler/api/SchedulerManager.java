package org.dromara.hodor.scheduler.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.scheduler.api.common.SchedulerConfig;

/**
 * scheduler manager
 *
 * @author tangzy
 * @since 1.0
 */
public final class SchedulerManager {

    private static final SchedulerManager INSTANCE = new SchedulerManager();

    private final ReentrantLock lock;

    private final Map<String, HodorScheduler> activeSchedulerMap;

    private final Map<String, HodorScheduler> standBySchedulerMap;

    private final Map<String, DataInterval> schedulerDataInterval;

    private final ExtensionLoader<HodorScheduler> extensionLoader;

    private SchedulerManager() {
        this.lock = new ReentrantLock();
        this.activeSchedulerMap = new ConcurrentHashMap<>();
        this.standBySchedulerMap = new ConcurrentHashMap<>();
        this.schedulerDataInterval = new ConcurrentHashMap<>();
        this.extensionLoader = ExtensionLoader.getExtensionLoader(HodorScheduler.class, SchedulerConfig.class);
    }

    public static SchedulerManager getInstance() {
        return INSTANCE;
    }

    public HodorScheduler createScheduler(SchedulerConfig config) {
        return extensionLoader.getProtoJoin("scheduler", config);
    }

    public HodorScheduler createScheduler(String schedulerName, SchedulerConfig config) {
        return extensionLoader.getProtoJoin(schedulerName, config);
    }

    public String createSchedulerName(String serverId, Integer copySetId) {
        return StringUtils.format("HodorScheduler_{}_{}", serverId, copySetId);
    }

    public void addActiveScheduler(HodorScheduler scheduler) {
        lock.lock();
        try {
            standBySchedulerMap.remove(scheduler.getSchedulerName());
            activeSchedulerMap.putIfAbsent(scheduler.getSchedulerName(), scheduler);
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        } finally {
            lock.unlock();
        }
    }

    public void addStandByScheduler(HodorScheduler scheduler) {
        lock.lock();
        try {
            activeSchedulerMap.remove(scheduler.getSchedulerName());
            standBySchedulerMap.putIfAbsent(scheduler.getSchedulerName(), scheduler);
            if (scheduler.isStarted()) {
                scheduler.standby();
            }
        } finally {
            lock.unlock();
        }
    }

    public HodorScheduler getActiveScheduler(String schedulerName) {
        return activeSchedulerMap.get(schedulerName);
    }

    public HodorScheduler getStandbyScheduler(String schedulerName) {
        return standBySchedulerMap.get(schedulerName);
    }

    public HodorScheduler getScheduler(String schedulerName) {
        HodorScheduler scheduler = getActiveScheduler(schedulerName);
        if (scheduler == null) {
            scheduler = getStandbyScheduler(schedulerName);
        }
        return scheduler;
    }

    public DataInterval getSchedulerDataInterval(String schedulerName) {
        return schedulerDataInterval.get(schedulerName);
    }

    public void addSchedulerDataInterval(String schedulerName, DataInterval dataInterval) {
        schedulerDataInterval.put(schedulerName, dataInterval);
    }

    public HodorScheduler getOrCreateScheduler(SchedulerConfig config) {
        HodorScheduler scheduler = getScheduler(config.getSchedulerName());
        if (scheduler != null) {
            return scheduler;
        }
        return createScheduler(config);
    }

    public HodorScheduler createActiveSchedulerIfAbsent(String serverEndpoint, Integer copySetId, DataInterval activeDataInterval) {
        HodorScheduler activeScheduler = buildScheduler(serverEndpoint, copySetId);
        this.addActiveScheduler(activeScheduler);
        this.addSchedulerDataInterval(activeScheduler.getSchedulerName(), activeDataInterval);
        return activeScheduler;
    }

    public HodorScheduler createStandbySchedulerIfAbsent(String serverEndpoint, Integer copySetId, DataInterval standbyDataInterval) {
        HodorScheduler standbyScheduler = buildScheduler(serverEndpoint, copySetId);
        this.addStandByScheduler(standbyScheduler);
        this.addSchedulerDataInterval(standbyScheduler.getSchedulerName(), standbyDataInterval);
        return standbyScheduler;
    }

    public HodorScheduler buildScheduler(String serverEndpoint, Integer copySetId) {
        final SchedulerConfig config = SchedulerConfig.builder()
            .schedulerName(this.createSchedulerName(serverEndpoint, copySetId))
            .threadCount(ThreadUtils.availableProcessors() * 2)
            .misfireThreshold(3000)
            .build();
        return this.getOrCreateScheduler(config);
    }

}
