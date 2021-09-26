package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;

/**
 *  abstract job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public abstract class AbstractJobExecutor implements JobExecutor {

    @Override
    public void execute(HodorJobExecutionContext context) {
        try {
            // 生成任务调度id
            // 生成调度开始时间
            process(context);
        } catch (Exception e) {
            // 调度处理异常
            throw new HodorSchedulerException(e);
        }
    }

    public abstract void process(HodorJobExecutionContext context);

}
