package org.dromara.hodor.common.storage.cache;

import org.dromara.hodor.common.extension.SPI;

/**
 * hodor cache source
 *
 * @author tomgs
 * @version 1.0
 */
@SPI("cachesource")
public interface HodorCacheSource {

    /**
     * 获取缓存类型
     *
     * @return 缓存类型
     */
    String getCacheType();

    /**
     * 获取指定分组缓存操作对象
     * @param <K> key 类型
     * @param <V> value 类型
     * @return cacheSource对象
     */
    <K, V> CacheClient<K, V> getCacheClient(String group);

    /**
     * 获取默认分组缓存对象
     * @param <K> key 类型
     * @param <V> value 类型
     * @return cacheSource对象
     */
    <K, V> CacheClient<K, V> getCacheClient();

}
