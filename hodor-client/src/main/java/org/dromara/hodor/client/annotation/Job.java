package org.dromara.hodor.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 调度任务描述
 *
 * @author tomgs
 * @since 2020/12/30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Job {

    /**
     * cron disabled这个用于不需要调度，但是需要注册的任务，可能用于任务的关联任务
     */
    String CRON_DISABLED = "-";

    /**
     * group name, default is class short name decapitate
     */
    String group() default "";

    /**
     * job name, default is class method name
     */
    String jobName() default "";

    /**
     * cron 表达式
     */
    String cron() default CRON_DISABLED;

    /**
     * 在配置了 cron 的基础上，如果需要立即执行，则设置为true, 默认是不立即执行的
     */
    boolean fireNow() default false;

    /**
     * job 执行超时时间设置。默认为3分钟，单位：s;
     */
    int timeout() default 180;

    /**
     * 任务是否广播执行
     */
    boolean isBroadcast() default false;

}
