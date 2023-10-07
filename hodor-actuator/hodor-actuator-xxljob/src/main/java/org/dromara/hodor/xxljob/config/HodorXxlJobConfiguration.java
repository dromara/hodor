package org.dromara.hodor.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.dromara.hodor.actuator.api.DefaultJobRegister;
import org.dromara.hodor.actuator.api.HodorActuatorManager;
import org.dromara.hodor.actuator.api.JobRegister;
import org.dromara.hodor.actuator.java.HodorJavaActuatorInit;
import org.dromara.hodor.actuator.java.ServiceProvider;
import org.dromara.hodor.actuator.java.annotation.HodorSchedulerAnnotationBeanPostProcessor;
import org.dromara.hodor.actuator.java.config.HodorActuatorJavaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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

    public HodorXxlJobConfiguration(final HodorActuatorJavaProperties hodorActuatorJavaProperties, final ApplicationContext applicationContext) {
        this.properties = hodorActuatorJavaProperties;
    }

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor() {
        return new XxlJobSpringExecutor();
    }

}
