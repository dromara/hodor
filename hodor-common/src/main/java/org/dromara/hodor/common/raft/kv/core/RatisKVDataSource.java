package org.dromara.hodor.common.raft.kv.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.common.storage.cache.CacheClient;
import org.dromara.hodor.common.storage.cache.CacheSourceConfig;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;

/**
 * RatisKVDataSource
 *
 * @author tomgs
 * @since 2022/3/22
 */
public class RatisKVDataSource implements HodorCacheSource {

    private final CacheSourceConfig cacheSourceConfig;

    private final Map<String, CacheClient<Object, Object>> groupCacheClientMap;

    public RatisKVDataSource(final CacheSourceConfig cacheSourceConfig) {
        this.cacheSourceConfig = cacheSourceConfig;
        this.groupCacheClientMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getCacheType() {
        return "ratiskv";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> CacheClient<K, V> getCacheClient(String group) {
        return (CacheClient<K, V>) groupCacheClientMap.computeIfAbsent(group, k -> new RatisVKClient<>(cacheSourceConfig));
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient() {
        return getCacheClient("default");
    }

}
