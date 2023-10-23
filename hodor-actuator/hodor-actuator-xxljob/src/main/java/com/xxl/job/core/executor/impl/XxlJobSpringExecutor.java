package com.xxl.job.core.executor.impl;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.JobRegister;
import org.dromara.hodor.actuator.api.core.JobInstance;
import org.dromara.hodor.actuator.java.ServiceProvider;
import org.dromara.hodor.model.enums.TimeType;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author xuxueli
 */
@Slf4j
public class XxlJobSpringExecutor implements SmartInitializingSingleton, DisposableBean {

    private final JobRegister jobRegister;

    public XxlJobSpringExecutor(final JobRegister jobRegister) {
        this.jobRegister = jobRegister;
    }

    @Override
    public void afterSingletonsInstantiated() {
        initJobHandlerRepository();
        log.info("initJobHandlerRepository finished");

        initJobHandlerMethodRepository();
        log.info("initJobHandlerMethodRepository finished");
    }

    @Override
    public void destroy() throws Exception {

    }

    private void initJobHandlerRepository() {
        // init job handler action
        Map<String, Object> serviceBeanMap = ServiceProvider.getInstance().getBeansWithAnnotation(JobHandler.class);
        if (serviceBeanMap.size() == 0) {
            return;
        }
        for (Object serviceBean : serviceBeanMap.values()) {
            if (serviceBean instanceof IJobHandler) {
                String groupName = serviceBean.getClass().getSimpleName();
                String jobName = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                IJobHandler handler = (IJobHandler) serviceBean;

                if (jobRegister.getJobDesc(JobKey.of(groupName, jobName)) != null) {
                    throw new RuntimeException("xxl-job jobhandler[" + jobName + "] naming conflicts.");
                }

                JobDesc jobDesc = JobDesc.builder()
                    .groupName(groupName)
                    .jobName(jobName)
                    .timeType(TimeType.NONE)
                    .jobCommandType("java")
                    .jobCommand(groupName + "#" + jobName)
                    .build();
                JobInstance jobInstance = JobInstance.builder()
                    .jobDesc(jobDesc)
                    .jobRunnable(handler)
                    .build();
                jobRegister.registerJob(jobInstance);
                log.info("xxl-job register jobhandler, name:{}", jobName);
            }
        }
    }

    public static void initJobHandlerMethodRepository() {
        final JobRegister jobRegister = ServiceProvider.getInstance().getBean(JobRegister.class);
        // init job handler from method
        String[] beanDefinitionNames = ServiceProvider.getInstance().getContext()
            .getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ServiceProvider.getInstance().getContext().getBean(beanDefinitionName);
            Map<Method, XxlJob> annotatedMethods = null;   // referred to ：org.springframework.context.event.EventListenerMethodProcessor.processBean
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            } catch (Throwable ex) {
                log.error("xxl-job method-jobhandler resolve error for bean[" + beanDefinitionName + "].", ex);
            }

            if (annotatedMethods == null || annotatedMethods.isEmpty()) {
                continue;
            }

            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();
                // regist
                registJobHandler(jobRegister, xxlJob, beanDefinitionName, bean, executeMethod);
            }
        }

    }

    private static void registJobHandler(JobRegister jobRegister, XxlJob xxlJob, String beanName, Object bean, Method executeMethod) {
        if (xxlJob == null) {
            return;
        }

        String jobName = xxlJob.value();
        //make and simplify the variables since they'll be called several times later
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        if (jobName.trim().length() == 0) {
            throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + clazz + "#" + methodName + "] .");
        }

        if (jobRegister.getJobDesc(JobKey.of(beanName, jobName)) != null) {
            throw new RuntimeException("xxl-job jobhandler[" + jobName + "] naming conflicts.");
        }

        executeMethod.setAccessible(true);

        // init and destroy
        Method initMethod = null;
        Method destroyMethod = null;

        if (xxlJob.init().trim().length() > 0) {
            try {
                initMethod = clazz.getDeclaredMethod(xxlJob.init());
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        if (xxlJob.destroy().trim().length() > 0) {
            try {
                destroyMethod = clazz.getDeclaredMethod(xxlJob.destroy());
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }

        JobDesc jobDesc = JobDesc.builder()
            .groupName(beanName)
            .timeType(TimeType.NONE)
            .jobName(jobName)
            .jobCommandType("java")
            .jobCommand(beanName + "#" + methodName)
            .build();
        JobInstance jobInstance = JobInstance.builder()
            .jobDesc(jobDesc)
            .jobRunnable(new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod))
            .build();
        jobRegister.registerJob(jobInstance);
        log.info("xxl-job register job, bean: {}, name:{}", beanName, jobName);
    }

}
