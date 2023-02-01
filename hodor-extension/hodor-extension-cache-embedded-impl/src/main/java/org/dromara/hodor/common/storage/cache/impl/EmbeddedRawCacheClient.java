package org.dromara.hodor.common.storage.cache.impl;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.dromara.hodor.common.concurrent.LockUtil;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.kv.core.HodorKVClient;
import org.dromara.hodor.common.storage.cache.CacheClient;
import org.dromara.hodor.common.utils.Pair;
import org.dromara.hodor.common.utils.ProtostuffUtils;

/**
 * registry raw cache source
 *
 * @author tomgs
 * @version 1.0
 */
public class EmbeddedRawCacheClient<K, V> implements CacheClient<K, V> {

    private final HodorKVClient hodorKVClient;

    private final ReadWriteLock readWriteLock;

    public EmbeddedRawCacheClient(final HodorRaftGroup hodorRaftGroup) {
        this.hodorKVClient = new HodorKVClient(hodorRaftGroup);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) {
        return LockUtil.lockMethod(readWriteLock.readLock(), k -> {
            final byte[] bytes = hodorKVClient.get(ProtostuffUtils.serialize(key));
            final Pair<V, Long> pair = ProtostuffUtils.deserialize(bytes, Pair.class);
            if (pair == null) {
                return null;
            }
            if (checkExpired(pair)) {
                delete(k);
                return null;
            }
            return pair.getFirst();
        }, key);
    }

    @Override
    public void put(K key, V value) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            final Pair<V, Long> valuePair = new Pair<>(value, -1L);
            hodorKVClient.put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(valuePair));
            return null;
        }, key, value);
    }

    @Override
    public void put(K key, V value, int expire) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            final Pair<V, Long> valuePair = new Pair<>(value, System.currentTimeMillis() + expire);
            hodorKVClient.put(ProtostuffUtils.serialize(key), ProtostuffUtils.serialize(valuePair));
            return null;
        }, key, value);
    }

    @Override
    public void delete(K key) {
        LockUtil.lockMethod(readWriteLock.writeLock(), k -> {
            hodorKVClient.delete(ProtostuffUtils.serialize(key));
            return null;
        }, key);
    }

    @Override
    public void clear() {

    }

    @Override
    public void close() throws IOException {
        hodorKVClient.close();
    }

}
