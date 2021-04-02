package org.dromara.hodor.server.executor;

import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 *  workflow job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public class FlowJobExecutor extends CommonJobExecutor {

    @Override
    public void process(HodorJobExecutionContext context) {
        //TODO: 校验一些与工作流相关的事项
        // 1、判断是否有正在运行的flow
        // 2、没有则获取flow json信息
        // 3、构建dag对象
        // 4、存储dag对象详情
        // 5、提交运行dag
        super.process(context);
    }

}