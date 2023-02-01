package org.dromara.hodor.common.storage.cache.impl;

import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.kv.core.HodorKVClient;
import org.dromara.hodor.common.storage.cache.CacheClient;
import org.dromara.hodor.common.utils.ProtostuffUtils;

/**
 * registry raw cache source
 *
 * @author tomgs
 * @version 1.0
 */
public class EmbeddedRawCacheClient<K, V> implements CacheClient<K, V> {

    private final HodorKVClient hodorKVClient;

    public EmbeddedRawCacheClient(final HodorRaftGroup hodorRaftGroup) {
        this.hodorKVClient = new HodorKVClient(hodorRaftGroup);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) {
        final byte[] bytes = hodorKVClient.get(ProtostuffUtils.serialize(key));
        return (V) ProtostuffUtils.deserialize(bytes, Object.class);
    }

    @Override
    public void put(K key, V value) {
        hodorKVClient.put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(value));
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
