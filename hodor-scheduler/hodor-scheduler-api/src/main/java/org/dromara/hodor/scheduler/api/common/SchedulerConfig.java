package org.dromara.hodor.scheduler.api.common;

import lombok.Builder;
import lombok.Getter;

/**
 * scheduler config
 *
 * @author tangzy
 */
@Builder
@Getter
public class SchedulerConfig {

    /**
     * scheduler名称
     */
    private final String schedulerName;

    /**
     * 线程数
     */
    private final int threadCount;

    /**
     * 错过执行阈值时间
     */
    private final int misfireThreshold;

}
