package org.dromara.hodor.server.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.dromara.hodor.common.concurrent.LockUtil;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;

/**
 * copy set manager
 *
 * @author tomgs
 * @since 1.0
 */
public enum CopySetManager {

    INSTANCE;

    private final Map<String, Set<CopySet>> leaderCopySetMap = Maps.newConcurrentMap();

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
        for (String server : servers) {
            if (!isCopySetLeader(server) && !StringUtils.equals(copySet.getLeader(), server)) {
                Set<CopySet> copySets = leaderCopySetMap.computeIfAbsent(server, k -> Sets.newHashSet());
                copySets.add(copySet);
                return server;
            }
        }
        // default copy sets leader
        return servers.get(0);
    }

    public void syncWithMetadata(HodorMetadata metadata) {
        LockUtil.lockMethod(lock.writeLock(), md -> {
            dataIntervalCopySetMap.clear();
            endpointCopySetListMap.clear();
            leaderCopySetMap.clear();
            Optional.ofNullable(md.getCopySets()).ifPresent(copySets -> copySets.forEach(copySet -> {
                leaderCopySetMap.computeIfAbsent(copySet.getLeader(), k -> Sets.newHashSet())
                    .add(copySet);
                dataIntervalCopySetMap.put(copySet.getDataInterval(), copySet);
                copySet.getServers().forEach(server -> {
                    Set<CopySet> sets = endpointCopySetListMap.computeIfAbsent(server, key -> Sets.newHashSet());
                    sets.add(copySet);
                });
            }));
            return null;
        }, metadata);
    }

    public boolean isCopySetLeader(String leader) {
        return leaderCopySetMap.containsKey(leader);
    }

    /**
     * 获取leader节点的CopySet，有时候一个节点会充当多个CopySet的主节点，一般情况是一个节点充当一个CopySet主节点
     *
     * @param leader copy set leader endpoint
     * @return CopySet Set
     */
    public Set<CopySet> getLeaderCopySet(String leader) {
        return leaderCopySetMap.get(leader);
    }

    public void clearCopySet() {
        leaderCopySetMap.clear();
    }

    public Optional<CopySet> getCopySetByInterval(Long hashId) {
        return LockUtil.lockMethod(lock.readLock(), k -> dataIntervalCopySetMap.entrySet().stream()
            .filter(entry -> entry.getKey().containsInterval(k))
            .map(Map.Entry::getValue)
            .findAny(), hashId);
    }

    public Optional<CopySet> getCopySetByInterval(DataInterval dataInterval) {
        return LockUtil.lockMethod(lock.readLock(), (k) -> Optional.ofNullable(dataIntervalCopySetMap.get(k)), dataInterval);
    }

    public List<CopySet> getCopySet(String endpoint) {
        return LockUtil.lockMethod(lock.readLock(),
            (k) -> ImmutableList.copyOf(Optional.ofNullable(endpointCopySetListMap.get(k)).orElse(Sets.newHashSet())),
            endpoint);
    }

    public void clearLeaderCopySet() {
        leaderCopySetMap.clear();
    }

}
