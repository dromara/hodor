package org.dromara.hodor.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.dromara.hodor.actuator.java.config.HodorActuatorJavaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hodor scheduler configuration
 *
 * @author tomgs
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(HodorActuatorJavaProperties.class)
public class HodorXxlJobConfiguration {

    private final HodorActuatorJavaProperties properties;

    public HodorXxlJobConfiguration(final HodorActuatorJavaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor() {
        return new XxlJobSpringExecutor();
    }

}
