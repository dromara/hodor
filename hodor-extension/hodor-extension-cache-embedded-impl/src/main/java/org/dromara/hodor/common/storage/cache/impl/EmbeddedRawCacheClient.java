package org.dromara.hodor.common.storage.cache.impl;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.dromara.hodor.common.concurrent.LockUtil;
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

    private final String cacheGroup;

    public EmbeddedRawCacheClient(final String serverAddresses, final String cacheGroup) {
        this.hodorKVClient = new HodorKVClient(serverAddresses);
        this.readWriteLock = new ReentrantReadWriteLock();
        this.cacheGroup = cacheGroup;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) {
        return LockUtil.lockMethod(readWriteLock.readLock(), k -> {
            final Pair<String, K> keyPair = new Pair<>(cacheGroup, key);
            final byte[] valueBytes = hodorKVClient.get(ProtostuffUtils.serialize(keyPair));
            final Pair<V, Long> valuePair = ProtostuffUtils.deserialize(valueBytes, Pair.class);
            if (valuePair == null) {
                return null;
            }
            if (checkExpired(valuePair)) {
                delete(k);
                return null;
            }
            return valuePair.getFirst();
        }, key);
    }

    @Override
    public void put(K key, V value) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            final Pair<String, K> keyPair = new Pair<>(cacheGroup, key);
            final Pair<V, Long> valuePair = new Pair<>(value, -1L);
            hodorKVClient.put(ProtostuffUtils.serialize(keyPair), ProtostuffUtils.serialize(valuePair));
            return null;
        }, key, value);
    }

    @Override
    public void put(K key, V value, int expire) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            final Pair<String, K> keyPair = new Pair<>(cacheGroup, key);
            final Pair<V, Long> valuePair = new Pair<>(value, System.currentTimeMillis() + expire);
            hodorKVClient.put(ProtostuffUtils.serialize(keyPair), ProtostuffUtils.serialize(valuePair));
            return null;
        }, key, value);
    }

    @Override
    public void delete(K key) {
        LockUtil.lockMethod(readWriteLock.writeLock(), k -> {
            final Pair<String, K> keyPair = new Pair<>(cacheGroup, key);
            hodorKVClient.delete(ProtostuffUtils.serialize(keyPair));
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
