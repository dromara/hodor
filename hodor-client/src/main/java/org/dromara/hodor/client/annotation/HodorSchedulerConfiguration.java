package org.dromara.hodor.client.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hodor scheduler configuration
 *
 * @author tomgs
 * @since 2020/12/30
 */
@Configuration
public class HodorSchedulerConfiguration {

    @Bean
    public HodorSchedulerAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new HodorSchedulerAnnotationBeanPostProcessor();
    }

}
