package org.dromara.hodor.common.storage.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.dromara.hodor.common.Tuple2;
import org.dromara.hodor.common.concurrent.LockUtil;

/**
 * LocalRawCacheSource
 *
 * @author tomgs
 * @since 2021/8/16
 */
public class LocalRawCacheSource<K, V> implements CacheSource<K, V> {

    private final Map<K, Tuple2<V, Long>> cache;

    private final ReadWriteLock readWriteLock;

    private final int maximumSize;

    public LocalRawCacheSource(final CacheSourceConfig cacheSourceConfig) {
        this.readWriteLock = new ReentrantReadWriteLock();
        maximumSize = cacheSourceConfig.getMaximumSize() <= 0 ? Integer.MAX_VALUE : cacheSourceConfig.getMaximumSize();
        this.cache = new LinkedHashMap<K, Tuple2<V, Long>>() {
            private static final long serialVersionUID = 4488509922354454855L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, Tuple2<V, Long>> eldest) {
                return size() > maximumSize;
            }
        };
    }

    @Override
    public V get(K key) {
        return LockUtil.lockMethod(readWriteLock.readLock(), k -> {
            Tuple2<V, Long> tuple = cache.get(k);
            if (tuple == null) {
                return null;
            }
            Long expireTime = tuple.getSecond();
            if (expireTime <= 0) {
                return tuple.getFirst();
            }
            // expired
            if (System.currentTimeMillis() - expireTime > 0) {
                remove(k);
                return null;
            }
            return tuple.getFirst();
        }, key);
    }

    @Override
    public void put(K key, V value) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            cache.put(k, new Tuple2<>(v, -1L));
            return null;
        }, key, value);
    }

    @Override
    public void put(K key, V value, int expire) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            Tuple2<V, Long> tuple = new Tuple2<>(v, System.currentTimeMillis() + expire);
            cache.put(k, tuple);
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
