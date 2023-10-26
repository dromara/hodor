package org.dromara.hodor.cache.embedded;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.cache.api.CacheSourceConfig;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.cache.api.CacheClient;
import org.dromara.hodor.cache.api.HodorCacheSource;

/**
 * EmbeddedCacheSource
 *
 * @author tomgs
 * @version 1.0
 */
@Join(order = 1)
public class EmbeddedCacheSource implements HodorCacheSource {

    private final String serverAddresses;

    private final Map<String, CacheClient<Object, Object>> groupCacheClientMap;

    public EmbeddedCacheSource(final CacheSourceConfig cacheSourceConfig) {
        Assert.notNull(cacheSourceConfig, "cacheSourceConfig must be not null.");
        Assert.notNull(cacheSourceConfig.getServerAddresses(), "cacheSourceConfig.serverAddresses must be not null");
        this.serverAddresses = cacheSourceConfig.getServerAddresses();
        this.groupCacheClientMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getCacheType() {
        return "embedded";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> CacheClient<K, V> getCacheClient(String group) {
        return (CacheClient<K, V>) groupCacheClientMap.computeIfAbsent(group,
            k -> new EmbeddedRawCacheClient<>(serverAddresses, group));
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient() {
        return getCacheClient("default");
    }

}
