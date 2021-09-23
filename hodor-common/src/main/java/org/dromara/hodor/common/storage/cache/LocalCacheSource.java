package org.dromara.hodor.common.storage.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.common.extension.Join;

/**
 * LocalCacheSource
 *
 * @author tomgs
 * @since 2021/8/16
 */
@Join
public class LocalCacheSource implements HodorCacheSource {

    private final CacheSourceConfig cacheSourceConfig;

    private final Map<String, CacheClient<Object, Object>> groupCacheClientMap;

    public LocalCacheSource(final CacheSourceConfig cacheSourceConfig) {
        this.cacheSourceConfig = cacheSourceConfig;
        this.groupCacheClientMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getCacheType() {
        return "local";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> CacheClient<K, V> getCacheClient(String group) {
        return (CacheClient<K, V>) groupCacheClientMap.computeIfAbsent(group, k -> new LocalRawCacheClient<>(cacheSourceConfig));
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient() {
        return getCacheClient("default");
    }

}
