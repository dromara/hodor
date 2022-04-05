package org.dromara.hodor.client.annotation;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.JobExecutionContext;
import org.dromara.hodor.client.JobRegistrar;
import org.dromara.hodor.client.core.JobInstance;
import org.dromara.hodor.client.core.ScheduledMethodRunnable;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
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

    public HodorSchedulerAnnotationBeanPostProcessor(final JobRegistrar registrar) {
        Assert.notNull(registrar, "JobRegistrar must be not null.");
        this.registrar = registrar;
    }

    @Override
    public void setEmbeddedValueResolver(@NonNull StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
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

    protected ScheduledMethodRunnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0 || method.getParameterCount() == 1, "A method annotated by @Job has at most one parameter");
        if (method.getParameterCount() == 1) {
            Class<?> parameterType = method.getParameterTypes()[0];
            if (!JobExecutionContext.class.isAssignableFrom(parameterType)) {
                throw new IllegalArgumentException("arg must be class JobExecutionContext");
            }
        }
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod, method.getParameterCount() == 1);
    }

    protected void processJob(Job job, Method method, Object bean) {
        ScheduledMethodRunnable runnable = createRunnable(bean, method);

        String groupName = job.group();
        if (!StringUtils.hasText(groupName)) {
            // 默认使用类的简化名称作为group
            groupName = Introspector.decapitalize(ClassUtils.getShortName(bean.getClass()));
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
                // TODO: 时区先不考虑
                //zone = this.embeddedValueResolver.resolveStringValue(zone);
            }
            if (StringUtils.hasLength(cron) && !Scheduled.CRON_DISABLED.equals(cron)) {
                Assert.isTrue(CronSequenceGenerator.isValidExpression(cron), String.format("cron [%s] xpression is invalid.", cron));
            }
        }

        boolean fireNow = job.fireNow();
        boolean broadcast = job.isBroadcast();
        int timeout = job.timeout();
        String commandType = job.commandType();

        JobInstance jobInstance = JobInstance.builder()
            .groupName(groupName)
            .jobName(jobName)
            .commandType(commandType)
            .cron(cron)
            .fireNow(fireNow)
            .broadcast(broadcast)
            .timeout(timeout)
            .build();

        registrar.addJob(jobInstance, runnable);
    }

}
