package org.dromara.hodor.common.storage.cache;

/**
 * cache client
 *
 * @author tomgs
 * @since 2021/8/11
 */
public interface CacheClient<K, V> {

    V get(K key);

    void put(K key, V value);

    void put(K key, V value, int expire);

    void remove(K key);

    void clear();

}
