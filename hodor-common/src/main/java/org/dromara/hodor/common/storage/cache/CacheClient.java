package org.dromara.hodor.common.storage.cache;

import org.dromara.hodor.common.utils.Pair;

/**
 * cache client
 *
 * @author tomgs
 * @since 1.0
 */
public interface CacheClient<K, V> extends AutoCloseable {

    default boolean checkExpired(Pair<V, Long> pair) {
        Long expireTime = pair.getSecond();
        if (expireTime <= 0) {
            return false;
        }
        // expired
        return System.currentTimeMillis() - expireTime > 0;
    }

    V get(K key);

    void put(K key, V value);

    void put(K key, V value, int expire);

    void delete(K key);

    void clear();

}
