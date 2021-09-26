package org.dromara.hodor.server.executor;

import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 *  time job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public class TimeJobExecutor extends CommonJobExecutor {

    @Override
    public void process(HodorJobExecutionContext context) {
        // TODO: 检查一些与定时相关的校验项
        // 1、判断任务的结束时间是否已经到了
        //
        super.process(context);
    }

}