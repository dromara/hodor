package org.dromara.hodor.server.config;

import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.cache.CacheSourceConfig;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;
import org.dromara.hodor.core.recoder.JobExecuteRecorder;
import org.dromara.hodor.core.recoder.LogJobExecuteRecorder;
import org.dromara.hodor.core.service.JobExecDetailService;
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

    private final HodorServerProperties properties;

    private final JobExecDetailService jobExecDetailService;

    public HodorServerConfiguration(HodorServerProperties properties, JobExecDetailService jobExecDetailService) {
        this.properties = properties;
        this.jobExecDetailService = jobExecDetailService;
    }

    @Bean
    public HodorCacheSource hodorCacheSource() {
        CacheSourceConfig sourceConfig = new CacheSourceConfig();
        return ExtensionLoader.getExtensionLoader(HodorCacheSource.class, CacheSourceConfig.class).getProtoJoin("cachesource", sourceConfig);
    }

    @Bean(destroyMethod = "stopReporterJobExecDetail")
    public JobExecuteRecorder jobExecuteRecorder() {
        LogJobExecuteRecorder logJobExecuteRecorder = new LogJobExecuteRecorder(properties.getLogDir(), jobExecDetailService, hodorCacheSource());
        logJobExecuteRecorder.startReporterJobExecDetail();
        return logJobExecuteRecorder;
    }

}
