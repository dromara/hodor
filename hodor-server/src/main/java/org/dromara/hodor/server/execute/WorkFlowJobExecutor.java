package org.dromara.hodor.server.execute;

import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 *  workflow job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public class WorkFlowJobExecutor extends CommonJobExecutor {

    @Override
    public void process(HodorJobExecutionContext context) {
        //TODO: 校验一些与工作流相关的事项
        super.process(context);
    }

}