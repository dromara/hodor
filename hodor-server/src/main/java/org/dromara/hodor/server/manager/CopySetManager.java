package org.dromara.hodor.server.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;

/**
 * copy set manager
 *
 * @author tomgs
 * @since 2020/7/28
 */
public enum CopySetManager {

    INSTANCE;

    private final Map<String, CopySet> leaderCopySetMap = Maps.newConcurrentMap();

    private final Map<DataInterval, CopySet> dataIntervalCopySetMap = Maps.newConcurrentMap();

    // endpoint -> List<CopySet>
    private final Map<String, Set<CopySet>> endpointCopySetListMap = Maps.newConcurrentMap();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static CopySetManager getInstance() {
        return INSTANCE;
    }

    /**
     * 从copySet中选出一个leader
     *
     * @param copySet copy set
     * @return leader endpoint
     */
    public String selectLeaderCopySet(CopySet copySet) {
        List<String> servers = copySet.getServers();
        // copy set leader election.
        servers.sort(Comparable::compareTo);
        for (String leader : servers) {
            if (!isCopySetLeader(leader)) {
                leaderCopySetMap.put(leader, copySet);
                return leader;
            }
        }
        // default copy sets leader
        return servers.get(0);
    }

    public void syncWithMetadata(HodorMetadata metadata) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            dataIntervalCopySetMap.clear();
            endpointCopySetListMap.clear();
            Optional.ofNullable(metadata.getCopySets()).ifPresent(copySets -> copySets.forEach(copySet -> {
                dataIntervalCopySetMap.put(copySet.getDataInterval(), copySet);
                copySet.getServers().forEach(server -> {
                    Set<CopySet> sets = endpointCopySetListMap.computeIfAbsent(server, k -> Sets.newHashSet());
                    sets.add(copySet);
                });
            }));
        } finally {
            lock.unlock();
        }
    }

    public boolean isCopySetLeader(String leader) {
        return leaderCopySetMap.containsKey(leader);
    }

    public CopySet getLeaderCopySet(String leader) {
        return leaderCopySetMap.get(leader);
    }

    public void clearCopySet() {
        leaderCopySetMap.clear();
    }

    public Optional<CopySet> getCopySetByInterval(Long hashId) {
        return readLockMethod(k -> dataIntervalCopySetMap.entrySet().stream()
            .filter(entry -> entry.getKey().containsInterval(k))
            .map(Map.Entry::getValue)
            .findAny(), hashId);
    }

    public Optional<CopySet> getCopySetByInterval(DataInterval dataInterval) {
        return readLockMethod((k) -> Optional.ofNullable(dataIntervalCopySetMap.get(k)), dataInterval);
    }

    public List<CopySet> getCopySet(String endpoint) {
        return readLockMethod((k) -> ImmutableList.copyOf(endpointCopySetListMap.get(k)), endpoint);
    }

    private <T, R> R readLockMethod(Function<T, R> function, T t) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return function.apply(t);
        } finally {
            lock.unlock();
        }
    }

}
