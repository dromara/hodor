package org.dromara.hodor.common.storage.cache.impl;

import org.dromara.hodor.common.storage.cache.CacheSource;

/**
 * registry raw cache source
 *
 * @author tomgs
 * @version 2021/8/30 1.0
 */
public class RegistryRawCacheSource<K, V> implements CacheSource<K, V> {

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
    public void remove(K key) {

    }

    @Override
    public void clear() {

    }

}
