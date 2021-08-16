package org.dromara.hodor.server.config;

import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.cache.CacheSourceConfig;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HodorServerConfiguration
 *
 * @author tomgs
 * @since 2021/8/16
 */
@Configuration
public class HodorServerConfiguration {

    @Bean
    public HodorCacheSource hodorCacheSource() {
        CacheSourceConfig sourceConfig = new CacheSourceConfig();
        return ExtensionLoader.getExtensionLoader(HodorCacheSource.class, CacheSourceConfig.class).getProtoJoin("cachesource", sourceConfig);
    }

}
