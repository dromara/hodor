package org.dromara.hodor.scheduler.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.scheduler.api.config.SchedulerConfig;

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

    private final Map<String, List<Long>> schedulerDataInterval;

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
                scheduler.shutdown();
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

    public List<Long> getSchedulerDataInterval(String schedulerName) {
        return schedulerDataInterval.get(schedulerName);
    }

    public void addSchedulerDataInterval(String schedulerName, List<Long> dataInterval) {
        schedulerDataInterval.put(schedulerName, dataInterval);
    }

}
