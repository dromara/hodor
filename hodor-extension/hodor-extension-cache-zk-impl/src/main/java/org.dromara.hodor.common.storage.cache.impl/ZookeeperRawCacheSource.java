package org.dromara.hodor.common.storage.cache.impl;

import org.dromara.hodor.common.storage.cache.CacheSource;

/**
 * zookeeper raw cache source
 *
 * @author tomgs
 * @since 2021/8/16
 */
public class ZookeeperRawCacheSource<K, V> implements CacheSource<K, V> {

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
