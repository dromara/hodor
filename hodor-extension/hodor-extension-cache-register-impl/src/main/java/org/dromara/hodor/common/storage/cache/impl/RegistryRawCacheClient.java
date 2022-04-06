package org.dromara.hodor.common.storage.cache.impl;

import org.dromara.hodor.common.storage.cache.CacheClient;

/**
 * registry raw cache source
 *
 * @author tomgs
 * @version 2021/8/30 1.0
 */
public class RegistryRawCacheClient<K, V> implements CacheClient<K, V> {

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public void put(K key, V value, int expire) {

    }

    @Override
    public void delete(K key) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void close() {

    }

}
