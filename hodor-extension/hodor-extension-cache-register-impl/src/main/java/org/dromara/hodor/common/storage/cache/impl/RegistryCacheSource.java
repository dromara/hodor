package org.dromara.hodor.common.storage.cache.impl;

import org.dromara.hodor.common.storage.cache.CacheSource;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;

/**
 * RegistryCacheSource
 *
 * @author tomgs
 * @version 2021/8/30 1.0
 */
public class RegistryCacheSource implements HodorCacheSource {

    @Override
    public String getCacheType() {
        return "registry";
    }

    @Override
    public <K, V> CacheSource<K, V> getCacheSource(String group) {
        return null;
    }

    @Override
    public <K, V> CacheSource<K, V> getCacheSource() {
        return null;
    }

}
