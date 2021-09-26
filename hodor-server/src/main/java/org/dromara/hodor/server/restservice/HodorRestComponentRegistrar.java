package org.dromara.hodor.server.restservice;

import java.beans.Introspector;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * registry hodor rest service processor
 *
 * @author tomgs
 */
public class HodorRestComponentRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String beanName = Introspector.decapitalize(HodorRestServiceProcessor.class.getSimpleName());
        BeanDefinition beanDefinition = BeanDefinitionBuilder
            .genericBeanDefinition(HodorRestServiceProcessor.class).getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

}
