package org.dromara.hodor.actuator.agent.config;

import org.dromara.hodor.actuator.agent.HodorAgentActuatorInit;
import org.dromara.hodor.actuator.agent.job.AgentJobRegister;
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
@EnableConfigurationProperties(HodorActuatorAgentProperties.class)
public class HodorSchedulerConfiguration {

    private final HodorActuatorAgentProperties properties;

    public HodorSchedulerConfiguration(final HodorActuatorAgentProperties hodorActuatorAgentProperties) {
        this.properties = hodorActuatorAgentProperties;
    }

    @Bean
    public JobRegister jobRegistrar() {
        return new AgentJobRegister(properties);
    }

    @Bean
    public HodorActuatorManager hodorActuatorManager() {
        return new HodorActuatorManager(properties.getCommonProperties(), jobRegistrar());
    }

    @Bean
    public HodorAgentActuatorInit hodorBigdataActuatorInit() {
        return new HodorAgentActuatorInit(hodorActuatorManager());
    }

}
