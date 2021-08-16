package org.dromara.hodor.common.storage.cache;

import org.dromara.hodor.common.extension.Join;

/**
 * @author tomgs
 * @since 2021/8/16
 */
@Join
public class LocalCacheSource implements HodorCacheSource {

    private final CacheSource<Object, Object> cacheSource;

    public LocalCacheSource(final CacheSourceConfig cacheSourceConfig) {
        this.cacheSource = new LocalRawCacheSource<>(cacheSourceConfig);
    }

    @Override
    public String getCacheType() {
        return "local";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> CacheSource<K, V> getCacheSource() {
        return (CacheSource<K, V>) cacheSource;
    }

}
