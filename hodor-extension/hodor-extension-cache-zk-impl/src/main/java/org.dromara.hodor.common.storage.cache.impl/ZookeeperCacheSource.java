package org.dromara.hodor.common.storage.cache.impl;

import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.storage.cache.CacheSource;
import org.dromara.hodor.common.storage.cache.CacheSourceConfig;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;

/**
 * ZookeeperCacheSource
 *
 * @author tomgs
 * @since 2021/8/16
 */
@Join
public class ZookeeperCacheSource implements HodorCacheSource {

    private final CacheSourceConfig cacheSourceConfig;

    public ZookeeperCacheSource(final CacheSourceConfig cacheSourceConfig) {
        this.cacheSourceConfig = cacheSourceConfig;
    }

    @Override
    public String getCacheType() {
        return "zookeeper";
    }

    @Override
    public <K, V> CacheSource<K, V> getCacheSource(String groupName) {
        return null;
    }

    @Override
    public <K, V> CacheSource<K, V> getCacheSource() {
        return new ZookeeperRawCacheSource<>();
    }

}
