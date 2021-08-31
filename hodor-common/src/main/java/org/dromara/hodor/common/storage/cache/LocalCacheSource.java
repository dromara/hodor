package org.dromara.hodor.common.storage.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.common.extension.Join;

/**
 * @author tomgs
 * @since 2021/8/16
 */
@Join
public class LocalCacheSource implements HodorCacheSource {

    private final CacheSourceConfig cacheSourceConfig;

    private final Map<String, CacheSource<Object, Object>> groupCacheSourceMap;

    public LocalCacheSource(final CacheSourceConfig cacheSourceConfig) {
        this.cacheSourceConfig = cacheSourceConfig;
        this.groupCacheSourceMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getCacheType() {
        return "local";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> CacheSource<K, V> getCacheSource(String groupName) {
        return (CacheSource<K, V>) groupCacheSourceMap.computeIfAbsent(groupName, k -> new LocalRawCacheSource<>(cacheSourceConfig));
    }

    @Override
    public <K, V> CacheSource<K, V> getCacheSource() {
        return getCacheSource("default");
    }

}
