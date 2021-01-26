package org.dromara.hodor.common.executor;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * executor info
 *
 * @author tomgs
 * @since 2021/1/25
 */
@Data
@Builder
public class ExecutorInfo implements Serializable {

    private static final long serialVersionUID = -7910941077144237921L;

    /**
     * 线程池名称
     */
    private String executorName;

    /**
     * 线程池核心线程数
     */
    private int coreThreadSize;

    /**
     * 最大线程池大小
     */
    private int maximumPoolSize;

    /**
     * 线程池中活动线程池大小
     */
    private int currentThreadSize;

    /**
     * 最大线程池线程大小
     */
    private int largestPoolSize;

    /**
     * 正在执行的任务数
     */
    private int activeTaskCount;

    /**
     * 在队列中等待执行的任务数
     */
    private int waitTaskCount;

    /**
     * 已经调度完成的task count
     */
    private long completeTaskCount;

    /**
     * 总的被调度的task count = complete + working + in queue
     */
    private long taskCount;

    /**
     * 阻塞队列大小
     */
    private long queueSize;

    /**
     * 阻塞队列容量
     */
    private long queueCapacity;

    /**
     * 任务队列任务数，待放到线程池的任务
     */
    private int circleQueueSize;

    /**
     * 任务队列任务数容量
     */
    private int circleQueueCapacity;

    /**
     * 任务被拒绝次数
     */
    private int rejectCount;

}
