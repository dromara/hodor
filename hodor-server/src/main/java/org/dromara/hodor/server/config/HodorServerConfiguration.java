package org.dromara.hodor.server.config;

import org.dromara.hodor.cache.api.CacheSourceConfig;
import org.dromara.hodor.cache.api.HodorCacheSource;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.core.recoder.JobExecuteRecorder;
import org.dromara.hodor.core.recoder.LogJobExecuteRecorder;
import org.dromara.hodor.core.service.JobExecDetailService;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.server.executor.JobExecutorTypeManager;
import org.dromara.hodor.server.manager.JobOperatorManager;
import org.dromara.hodor.server.service.RegistryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * HodorServerConfiguration
 *
 * @author tomgs
 * @since 1.0
 */
@Configuration
public class HodorServerConfiguration {

    private final HodorServerProperties properties;

    private final JobExecDetailService jobExecDetailService;

    public HodorServerConfiguration(HodorServerProperties properties,
                                    JobExecDetailService jobExecDetailService) {
        this.properties = properties;
        this.jobExecDetailService = jobExecDetailService;
    }

    @Bean
    public HodorCacheSource hodorCacheSource() {
        CacheSourceConfig sourceConfig = Optional.ofNullable(properties.getCacheSource())
            .orElseThrow(() -> new IllegalArgumentException("cache source config must be not null."));
        ;
        return ExtensionLoader.getExtensionLoader(HodorCacheSource.class, CacheSourceConfig.class)
            .getProtoJoin(sourceConfig.getType(), sourceConfig);
    }

    @Bean
    public RegistryCenter registryCenter() {
        final HodorServerProperties.RegistryProperties registryProperties = Optional.ofNullable(properties.getRegistry())
            .orElseThrow(() -> new IllegalArgumentException("registry config must be not null."));
        return ExtensionLoader.getExtensionLoader(RegistryCenter.class).getJoin(registryProperties.getType());
    }

    @Bean(destroyMethod = "stopReporterJobExecDetail")
    public JobExecuteRecorder jobExecuteRecorder() {
        LogJobExecuteRecorder logJobExecuteRecorder = new LogJobExecuteRecorder(properties.getLogDir(), jobExecDetailService, hodorCacheSource());
        logJobExecuteRecorder.startReporterJobExecDetail();
        return logJobExecuteRecorder;
    }

    @Bean
    public JobOperatorManager jobOperatorManager(RegistryService registryService) {
        return new JobOperatorManager(
            JobExecutorTypeManager.getInstance(),
            SchedulerManager.getInstance(),
            registryService);
    }

}
