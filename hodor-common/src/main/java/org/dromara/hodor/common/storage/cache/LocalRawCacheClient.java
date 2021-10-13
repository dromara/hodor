package org.dromara.hodor.common.storage.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.dromara.hodor.common.utils.Pair;
import org.dromara.hodor.common.concurrent.LockUtil;

/**
 * LocalRawCacheClient
 *
 * @author tomgs
 * @since 2021/8/16
 */
public class LocalRawCacheClient<K, V> implements CacheClient<K, V> {

    private final Map<K, Pair<V, Long>> cache;

    private final ReadWriteLock readWriteLock;

    private final int maximumSize;

    public LocalRawCacheClient(final CacheSourceConfig cacheSourceConfig) {
        this.readWriteLock = new ReentrantReadWriteLock();
        maximumSize = cacheSourceConfig.getMaximumSize() <= 0 ? Integer.MAX_VALUE : cacheSourceConfig.getMaximumSize();
        this.cache = new LinkedHashMap<K, Pair<V, Long>>() {
            private static final long serialVersionUID = 4488509922354454855L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, Pair<V, Long>> eldest) {
                return size() > maximumSize;
            }
        };
    }

    @Override
    public V get(K key) {
        return LockUtil.lockMethod(readWriteLock.readLock(), k -> {
            Pair<V, Long> pair = cache.get(k);
            if (pair == null) {
                return null;
            }
            Long expireTime = pair.getSecond();
            if (expireTime <= 0) {
                return pair.getFirst();
            }
            // expired
            if (System.currentTimeMillis() - expireTime > 0) {
                remove(k);
                return null;
            }
            return pair.getFirst();
        }, key);
    }

    @Override
    public void put(K key, V value) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            cache.put(k, new Pair<>(v, -1L));
            return null;
        }, key, value);
    }

    @Override
    public void put(K key, V value, int expire) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            Pair<V, Long> pair = new Pair<>(v, System.currentTimeMillis() + expire);
            cache.put(k, pair);
            return null;
        }, key, value);
    }

    @Override
    public void remove(K key) {
        LockUtil.lockMethod(readWriteLock.writeLock(), k -> {
            cache.remove(k);
            return null;
        }, key);
    }

    @Override
    public void clear() {
        LockUtil.lockMethod(readWriteLock.writeLock(), k -> {
            cache.clear();
            return null;
        }, null);
    }

}
