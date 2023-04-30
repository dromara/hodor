package org.dromara.hodor.actuator.bigdata.config;

import org.dromara.hodor.actuator.bigdata.HodorBigdataActuatorInit;
import org.dromara.hodor.actuator.bigdata.register.BigdataJobRegister;
import org.dromara.hodor.actuator.api.HodorActuatorManager;
import org.dromara.hodor.actuator.api.JobRegister;
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
@EnableConfigurationProperties(HodorActuatorBigdataProperties.class)
public class HodorSchedulerConfiguration {

    private final HodorActuatorBigdataProperties properties;

    public HodorSchedulerConfiguration(final HodorActuatorBigdataProperties hodorActuatorBigdataProperties) {
        this.properties = hodorActuatorBigdataProperties;
    }

    @Bean
    public JobRegister jobRegistrar() {
        return new BigdataJobRegister(properties);
    }

    @Bean
    public HodorActuatorManager hodorActuatorManager() {
        return new HodorActuatorManager(properties.getCommonProperties(), jobRegistrar());
    }

    @Bean
    public HodorBigdataActuatorInit hodorBigdataActuatorInit() {
        return new HodorBigdataActuatorInit(hodorActuatorManager());
    }

}
