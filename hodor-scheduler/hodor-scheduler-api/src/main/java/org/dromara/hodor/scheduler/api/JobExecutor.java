package org.dromara.hodor.scheduler.api;

/**
 * @author tomgs
 * @version 2020/6/25 1.0
 */
public interface JobExecutor {

    void execute(HodorSchedulerContext context);

}
