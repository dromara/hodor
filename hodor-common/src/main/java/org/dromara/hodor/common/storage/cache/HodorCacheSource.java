package org.dromara.hodor.common.storage.cache;

import org.dromara.hodor.common.extension.SPI;

/**
 * hodor cache source
 *
 * @author tomgs
 * @since 2021/8/11
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
     * 获取缓存操作对象
     * @return 缓存实例
     */
    <K, V> CacheSource<K, V> getCacheSource();

}
