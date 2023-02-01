package org.dromara.hodor.common.storage.cache.impl;

import cn.hutool.core.lang.Assert;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.kv.core.KVConstant;
import org.dromara.hodor.common.storage.cache.CacheClient;
import org.dromara.hodor.common.storage.cache.CacheSourceConfig;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;

/**
 * EmbeddedCacheSource
 *
 * @author tomgs
 * @version 1.0
 */
@Join
public class EmbeddedCacheSource implements HodorCacheSource {

    private final CacheSourceConfig cacheSourceConfig;

    private final HodorRaftGroup hodorRaftGroup;

    public EmbeddedCacheSource(final CacheSourceConfig cacheSourceConfig) {
        Assert.notNull(cacheSourceConfig, "cacheSourceConfig must be not null.");
        Assert.notNull(cacheSourceConfig.getServerAddresses(), "cacheSourceConfig.serverAddresses must be not null");
        this.cacheSourceConfig = cacheSourceConfig;
        this.hodorRaftGroup = HodorRaftGroup.builder()
            .raftGroupName(KVConstant.HODOR_KV_GROUP_NAME)
            .addresses(cacheSourceConfig.getServerAddresses())
            .build();
    }

    @Override
    public String getCacheType() {
        return "embedded";
    }

    @Override
    //@SuppressWarnings("unchecked")
    public <K, V> CacheClient<K, V> getCacheClient(String group) {
        final EmbeddedRawCacheClient<K, V> embeddedRawCacheClient = new EmbeddedRawCacheClient<>(hodorRaftGroup);
        return embeddedRawCacheClient;
    }

    @Override
    public <K, V> CacheClient<K, V> getCacheClient() {
        return null;
    }

}
