package org.dromara.hodor.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

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
     * group name
     */
    String group() default "";

    /**
     * job name
     */
    String jobName() default "";

    /**
     * cron 表达式
     */
    String cron();

    /**
     * 在配置了 cron 的基础上，如果需要立即执行，则设置为true, 默认是不立即执行的
     */
    boolean fireNow() default false;

    /**
     * job 执行超时时间设置。默认为3分钟，单位：s;
     */
    int timeout() default 180;

    /**
     * 超时时间单位，默认为 s
     */
    TimeUnit timeunit() default TimeUnit.SECONDS;

    /**
     * 任务是否广播执行
     */
    boolean isBroadcast() default false;

}
