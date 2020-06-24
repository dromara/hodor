package org.dromara.hodor.scheduler.api.executor;

import org.dromara.hodor.scheduler.api.HodorSchedulerContext;
import org.dromara.hodor.scheduler.api.JobExecutor;

/**
 *  
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public abstract class AbstractJobExecutor implements JobExecutor {

    @Override
    public void execute(HodorSchedulerContext context) {
        try {
            // 生成任务调度id
            // 生成调度开始时间
            // process(context, map);
            process(context);
        } catch (Exception e) {
            // 调度处理异常
        }
    }

    public abstract void process(HodorSchedulerContext context);

}
