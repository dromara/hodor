package org.dromara.hodor.client.annotation;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.config.JobRegistrar;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * annotation handler<br/>
 *
 * handler annotation Job and compatible with Spring Scheduled annotations
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 *
 * @author tomgs
 * @since 2020/12/30
 */
@Slf4j
public class HodorSchedulerAnnotationBeanPostProcessor implements BeanPostProcessor, EmbeddedValueResolverAware {

    private final JobRegistrar registrar;

    @Nullable
    private StringValueResolver embeddedValueResolver;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    public HodorSchedulerAnnotationBeanPostProcessor() {
        this.registrar = new JobRegistrar();
    }

    public HodorSchedulerAnnotationBeanPostProcessor(final JobRegistrar registrar) {
        Assert.notNull(registrar, "JobRegistrar must be not null.");
        this.registrar = registrar;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopInfrastructureBean || bean instanceof TaskScheduler ||
            bean instanceof ScheduledExecutorService) {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            //Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass, (MethodIntrospector.MetadataLookup<Set<Scheduled>>) method -> {
            //    Set<Scheduled> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Scheduled.class, Schedules.class);
            //    return (!scheduledMethods.isEmpty() ? scheduledMethods : null);
            //});

            Map<Method, Set<Job>> annotatedMethods = MethodIntrospector.selectMethods(targetClass, (MethodIntrospector.MetadataLookup<Set<Job>>) method -> {
                Set<Job> jobMethods = AnnotatedElementUtils.getAllMergedAnnotations(method, Job.class);
                return (!jobMethods.isEmpty() ? jobMethods : null);
            });

            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (log.isTraceEnabled()) {
                    log.trace("No @Job annotations found on bean class: " + targetClass);
                }
            }
            else {
                // Non-empty set of methods
                annotatedMethods.forEach((method, scheduledMethods) ->
                    scheduledMethods.forEach(job -> processJob(job, method, bean)));
                if (log.isTraceEnabled()) {
                    log.trace(annotatedMethods.size() + " @Job methods processed on bean '" + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    protected void processJob(Job job, Method method, Object bean) {
        String group = job.group();
        if (!StringUtils.hasText(group)) {
            // 默认使用类的简化名称作为group
            group = Introspector.decapitalize(ClassUtils.getShortName(bean.getClass()));
        }

        String jobName = job.jobName();
        if (!StringUtils.hasText(jobName)) {
            // 默认使用方法名作为任务名称
            jobName = method.getName();
        }

        // check cron expresion
        String cron = job.cron();
        if (StringUtils.hasText(cron)) {
            if (this.embeddedValueResolver != null) {
                cron = this.embeddedValueResolver.resolveStringValue(cron);
                //TODO: 时区先不考虑
                //zone = this.embeddedValueResolver.resolveStringValue(zone);
            }
        }

        boolean fireNow = job.fireNow();
        boolean broadcast = job.isBroadcast();
        int timeout = job.timeout();



    }

}
