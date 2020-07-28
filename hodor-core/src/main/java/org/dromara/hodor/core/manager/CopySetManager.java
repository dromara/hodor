package org.dromara.hodor.core.manager;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.dromara.hodor.core.entity.CopySet;

/**
 * copy set manager
 *
 * @author tomgs
 * @since 2020/7/28
 */
public enum CopySetManager {
    INSTANCE;

    private final Set<String> leaderCopySet = Sets.newConcurrentHashSet();

    public static CopySetManager getInstance() {
        return INSTANCE;
    }

    public String selectLeaderCopySet(CopySet copySet) {
        List<String> servers = copySet.getServers();
        // copy set leader election.
        servers.sort(Comparable::compareTo);
        for (String leader : servers) {
            if (!isCopySetLeader(leader)) {
                leaderCopySet.add(leader);
                return leader;
            }
        }
        // default copy sets leader
        return servers.get(0);
    }

    public boolean isCopySetLeader(String leader) {
        return leaderCopySet.contains(leader);
    }

    public void clearCopySet() {
        leaderCopySet.clear();
    }
}
