package org.dromara.hodor.actuator.java.annotation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.JobExecutionContext;
import org.dromara.hodor.actuator.api.JobRegister;
import org.dromara.hodor.actuator.api.core.JobInstance;
import org.dromara.hodor.actuator.java.core.ScheduledMethodRunnable;
import org.dromara.hodor.actuator.java.job.JavaJob;
import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobDesc;
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
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * annotation handler<br/>
 *
 * handler annotation Job and compatible with Spring Scheduled annotations
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorSchedulerAnnotationBeanPostProcessor implements BeanPostProcessor, EmbeddedValueResolverAware {

    private final JobRegister registrar;

    @Nullable
    private StringValueResolver embeddedValueResolver;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    public HodorSchedulerAnnotationBeanPostProcessor(final JobRegister registrar) {
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
        String jobName = job.jobName();
        // check cron expresion
        String cron = job.cron();
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
        TimeType timeType = TimeType.CRON;
        String timeExp = cron;

        if (Scheduled.CRON_DISABLED.equals(cron)) {
            timeType = TimeType.NONE;
        } else if (job.fixedDelay() > -1) {
            timeType = TimeType.FIXED_DELAY;
            timeExp = String.valueOf(job.fixedDelay());
        } else if (job.fixedRate() > -1) {
            timeType = TimeType.FIXED_RATE;
            timeExp = String.valueOf(job.fixedRate());
        }

        boolean fireNow = job.fireNow();
        boolean misfire = job.misfire();
        boolean failover = job.failover();
        int timeout = job.timeout();
        String commandType = job.commandType();
        String command = job.command();

        JobDesc jobDesc = JobDesc.builder()
            .groupName(groupName)
            .jobName(jobName)
            .jobCommandType(commandType)
            .jobCommand(command)
            .timeType(timeType)
            .timeExp(timeExp)
            .fireNow(fireNow)
            .misfire(misfire)
            .failover(failover)
            .timeout(timeout)
            .build();

        JobInstance jobInstance = JobInstance.builder()
            .jobDesc(jobDesc)
            .jobRunnable(new JavaJob(runnable))
            .build();

        registrar.registerJob(jobInstance);
    }

}
