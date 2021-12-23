package org.dromara.hodor.actuator.java.config;

import org.dromara.hodor.actuator.common.DefaultJobRegistrar;
import org.dromara.hodor.actuator.common.HodorActuatorManager;
import org.dromara.hodor.actuator.common.JobRegistrar;
import org.dromara.hodor.actuator.java.HodorJavaActuatorInit;
import org.dromara.hodor.actuator.java.ServiceProvider;
import org.dromara.hodor.actuator.java.annotation.HodorSchedulerAnnotationBeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hodor scheduler configuration
 *
 * @author tomgs
 * @since 2020/12/30
 */
@Configuration
@EnableConfigurationProperties(HodorActuatorJavaProperties.class)
public class HodorSchedulerConfiguration {

    private final HodorActuatorJavaProperties properties;

    public HodorSchedulerConfiguration(final HodorActuatorJavaProperties hodorActuatorJavaProperties, final ApplicationContext applicationContext) {
        this.properties = hodorActuatorJavaProperties;
        ServiceProvider.getInstance().setApplicationContext(applicationContext);
    }

    @Bean
    public JobRegistrar jobRegistrar() {
        return new DefaultJobRegistrar();
    }

    @Bean
    public HodorSchedulerAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new HodorSchedulerAnnotationBeanPostProcessor(jobRegistrar());
    }

    @Bean
    public HodorActuatorManager hodorActuatorManager() {
        return new HodorActuatorManager(properties.getCommonProperties(), jobRegistrar());
    }

    @Bean
    public HodorJavaActuatorInit hodorClientInit() {
        return new HodorJavaActuatorInit(hodorActuatorManager());
    }

}