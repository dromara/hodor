package org.dromara.hodor.common.storage.cache.impl;

import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.storage.cache.CacheClient;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;

/**
 * RegistryCacheSource
 *
 * @author tomgs
 * @version 2021/8/30 1.0
 */
@Join
public class RegistryCacheSource implements HodorCacheSource {

    @Override
    public String getCacheType() {
        return "registry";
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient(String group) {
        return null;
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient() {
        return null;
    }

}
