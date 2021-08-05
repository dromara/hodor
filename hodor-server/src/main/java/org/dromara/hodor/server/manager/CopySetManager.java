package org.dromara.hodor.server.manager;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;

/**
 * copy set manager
 *
 * @author tomgs
 * @since 2020/7/28
 */
public enum CopySetManager {

    INSTANCE;

    private final Map<String, CopySet> leaderCopySet = Maps.newConcurrentMap();

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
                leaderCopySet.put(leader, copySet);
                return leader;
            }
        }
        // default copy sets leader
        return servers.get(0);
    }

    public void syncWithMetadata(HodorMetadata metadata) {
        /*Optional.ofNullable(metadata.getCopySets()).ifPresent(copySets -> copySets.forEach(copySet -> {
            leaderCopySet.put(copySet.getLeader(), copySet);
        }));*/
    }

    public boolean isCopySetLeader(String leader) {
        return leaderCopySet.containsKey(leader);
    }

    public CopySet getCopySet(String leader) {
        return leaderCopySet.get(leader);
    }

    public void clearCopySet() {
        leaderCopySet.clear();
    }

    public CopySet getCopySetByInterval(Long hashId) {
        return null;
    }

}
