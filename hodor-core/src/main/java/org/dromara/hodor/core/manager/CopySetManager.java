package org.dromara.hodor.core.manager;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.dromara.hodor.core.CopySet;

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

    public boolean isCopySetLeader(String leader) {
        return leaderCopySet.containsKey(leader);
    }

    public CopySet getCopySet(String leader) {
        return leaderCopySet.get(leader);
    }

    public void clearCopySet() {
        leaderCopySet.clear();
    }
}
