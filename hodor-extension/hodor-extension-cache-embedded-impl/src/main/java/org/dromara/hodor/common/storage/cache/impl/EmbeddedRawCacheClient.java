package org.dromara.hodor.common.storage.cache.impl;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.dromara.hodor.common.concurrent.LockUtil;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.RaftUtils;
import org.dromara.hodor.common.raft.kv.core.HodorKVClient;
import org.dromara.hodor.common.raft.kv.core.KVConstant;
import org.dromara.hodor.common.raft.kv.storage.DBColumnFamily;
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

    private final String tableName;

    public EmbeddedRawCacheClient(final String serverAddresses, final String cacheGroup) {
        RaftUtils.assertRaftGroupAddress(serverAddresses);
        HodorRaftGroup hodorRaftGroup = HodorRaftGroup.builder()
            .raftGroupName(KVConstant.HODOR_KV_GROUP_NAME)
            .addresses(serverAddresses)
            .build();
        this.hodorKVClient = new HodorKVClient(hodorRaftGroup);
        this.readWriteLock = new ReentrantReadWriteLock();
        this.cacheGroup = cacheGroup;
        this.tableName = DBColumnFamily.Default.getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) {
        return LockUtil.lockMethod(readWriteLock.readLock(), k -> {
            final Pair<String, K> keyPair = new Pair<>(cacheGroup, key);
            final byte[] valueBytes = hodorKVClient.kvOperator(tableName)
                .get(ProtostuffUtils.serialize(keyPair));
            if (valueBytes == null) {
                return null;
            }
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
            hodorKVClient.kvOperator(tableName)
                .put(ProtostuffUtils.serialize(keyPair), ProtostuffUtils.serialize(valuePair));
            return null;
        }, key, value);
    }

    @Override
    public void put(K key, V value, int expire) {
        LockUtil.lockMethod(readWriteLock.writeLock(), (k ,v) -> {
            final Pair<String, K> keyPair = new Pair<>(cacheGroup, key);
            final Pair<V, Long> valuePair = new Pair<>(value, System.currentTimeMillis() + expire);
            hodorKVClient.kvOperator(tableName)
                .put(ProtostuffUtils.serialize(keyPair), ProtostuffUtils.serialize(valuePair));
            return null;
        }, key, value);
    }

    @Override
    public void delete(K key) {
        LockUtil.lockMethod(readWriteLock.writeLock(), k -> {
            final Pair<String, K> keyPair = new Pair<>(cacheGroup, key);
            hodorKVClient.kvOperator(tableName)
                .delete(ProtostuffUtils.serialize(keyPair));
            return null;
        }, key);
    }

    @Override
    public void clear() {

    }

    @Override
    public void close() throws Exception {
        hodorKVClient.close();
    }

}
