package org.dromara.hodor.springtask.processor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.JobRegister;
import org.dromara.hodor.actuator.api.core.JobInstance;
import org.dromara.hodor.actuator.java.core.ScheduledMethodRunnable;
import org.dromara.hodor.actuator.java.core.ScheduledMethods;
import org.dromara.hodor.actuator.java.job.JavaJob;
import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobDesc;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * annotation handler<br/>
 * <p>
 * handler annotation Job and compatible with Spring Scheduled annotations
 *
 * @author tomgs
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 * @since 1.0
 */
@Slf4j
public class HodorScheduledBeanPostProcessor implements BeanPostProcessor, EmbeddedValueResolverAware {

    private final JobRegister registrar;

    @Nullable
    private StringValueResolver embeddedValueResolver;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    public HodorScheduledBeanPostProcessor(final JobRegister registrar) {
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
            Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass, (MethodIntrospector.MetadataLookup<Set<Scheduled>>) method -> {
                Set<Scheduled> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Scheduled.class, Schedules.class);
                return (!scheduledMethods.isEmpty() ? scheduledMethods : null);
            });

            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (log.isTraceEnabled()) {
                    log.trace("No @Scheduled annotations found on bean class: " + targetClass);
                }
            } else {
                // Non-empty set of methods
                annotatedMethods.forEach((method, scheduledMethods) ->
                    scheduledMethods.forEach(scheduled -> processScheduled(scheduled, method, bean)));
                if (log.isTraceEnabled()) {
                    log.trace(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    private void processScheduled(Scheduled scheduled, Method method, Object bean) {
        ScheduledMethodRunnable runnable = ScheduledMethods.createRunnable(bean, method);
        String groupName = bean.getClass().getSimpleName();
        String jobName = method.getName();
        // check cron expresion
        String cron = scheduled.cron();
        if (StringUtils.hasText(cron) && !Scheduled.CRON_DISABLED.equals(cron)) {
            if (this.embeddedValueResolver != null) {
                cron = this.embeddedValueResolver.resolveStringValue(cron);
                // TODO: 时区先不考虑
                //zone = this.embeddedValueResolver.resolveStringValue(zone);
            }
            if (StringUtils.hasLength(cron)) {
                Assert.isTrue(CronExpression.isValidExpression(cron), String.format("cron [%s] expression is invalid.", cron));
            }
        }
        final TimeUnit timeUnit = scheduled.timeUnit();
        TimeType timeType = TimeType.CRON;
        String timeExp = cron;

        if (Scheduled.CRON_DISABLED.equals(cron)) {
            timeType = TimeType.NONE;
        } else if (scheduled.fixedDelay() > -1) {
            timeType = TimeType.FIXED_DELAY;
            timeExp = String.valueOf(TimeUnit.MILLISECONDS.convert(scheduled.fixedDelay(), timeUnit));
        } else if (scheduled.fixedRate() > -1) {
            timeType = TimeType.FIXED_RATE;
            timeExp = String.valueOf(TimeUnit.MILLISECONDS.convert(scheduled.fixedRate(), timeUnit));
        }

        JobDesc jobDesc = JobDesc.builder()
            .groupName(groupName)
            .jobName(jobName)
            .jobCommandType("springTask")
            .jobCommand(groupName + "#" + jobName)
            .timeType(timeType)
            .timeExp(timeExp)
            .fireNow(false)
            .misfire(false)
            .failover(false)
            .timeout(180)
            .build();

        JobInstance jobInstance = JobInstance.builder()
            .jobDesc(jobDesc)
            .jobRunnable(new JavaJob(runnable))
            .build();

        registrar.registerJob(jobInstance);
    }

}
