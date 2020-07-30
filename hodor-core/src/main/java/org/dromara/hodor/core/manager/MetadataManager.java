package org.dromara.hodor.core.manager;

import java.util.concurrent.locks.ReentrantLock;
import org.dromara.hodor.core.entity.HodorMetadata;

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
