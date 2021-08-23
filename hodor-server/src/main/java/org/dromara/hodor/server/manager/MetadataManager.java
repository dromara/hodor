package org.dromara.hodor.server.manager;

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;

/**
 * metadata manager
 *
 * @author tomgs
 * @since 2020/7/28
 */
public enum MetadataManager {

    INSTANCE;

    private volatile HodorMetadata metadata;

    private final ReentrantLock lock = new ReentrantLock();

    public static MetadataManager getInstance() {
        return INSTANCE;
    }

    public void loadData(final HodorMetadata metadata) {
        lock.lock();
        try {
            if (isEqual(this.metadata, metadata)) {
                return;
            }
            this.metadata = metadata;
        } finally {
            lock.unlock();
        }
    }

    public HodorMetadata getMetadata() {
        return metadata;
    }

    public Map<String, Set<CopySet>> parseMetadata(final HodorMetadata metadata) {
        Map<String, Set<CopySet>> endpointCopySetListMap = new HashMap<>();
        Optional.ofNullable(metadata.getCopySets()).ifPresent(copySets -> copySets.forEach(copySet -> {
            copySet.getServers().forEach(server -> {
                Set<CopySet> sets = endpointCopySetListMap.computeIfAbsent(server, key -> Sets.newHashSet());
                sets.add(copySet);
            });
        }));
        return endpointCopySetListMap;
    }

    public boolean isEqual(HodorMetadata oldValue, HodorMetadata newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }

        if (oldValue == null) {
            return false;
        }

        return oldValue.equals(newValue);
    }

}
