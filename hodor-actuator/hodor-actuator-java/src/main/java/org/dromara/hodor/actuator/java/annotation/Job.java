package org.dromara.hodor.actuator.java.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dromara.hodor.common.cron.CronUtils;

/**
 * 调度任务描述
 *
 * @author tomgs
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Job {

    /**
     * group name, default is class short name decapitate
     */
    String group() default "";

    /**
     * job name, default is class method name
     */
    String jobName() default "";

    String commandType() default "java";

    String zone() default "";

    int fixedDelay() default -1;

    int fixedRate() default -1;

    /**
     * cron 表达式
     */
    String cron() default CronUtils.CRON_DISABLED;

    /**
     * 在配置了 cron 的基础上，如果需要立即执行，则设置为true, 默认是不立即执行的
     */
    boolean fireNow() default false;

    boolean misfire() default false;

    boolean failover() default false;

    /**
     * job 执行超时时间设置。默认为3分钟，单位：s;
     */
    int timeout() default 180;

}
