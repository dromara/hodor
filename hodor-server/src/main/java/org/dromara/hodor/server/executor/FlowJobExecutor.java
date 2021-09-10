package org.dromara.hodor.server.executor;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.core.dag.DagCreator;
import org.dromara.hodor.core.dag.NodeBean;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 *  workflow job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
@Slf4j
public class FlowJobExecutor extends CommonJobExecutor {

    private final FlowJobExecutorManager flowJobExecutorManager;

    public FlowJobExecutor() {
        this.flowJobExecutorManager = FlowJobExecutorManager.getInstance();
    }

    @Override
    public void process(HodorJobExecutionContext context) {
        // 1、判断是否有正在运行的flow
        // 2、没有则获取flow json信息
        // 3、构建dag对象
        // 4、存储dag对象详情
        // 5、提交运行dag
        if (isAlreadyRunningJob(context.getJobKey())) {
            log.error("dag {} is already running.", context.getJobKey());
            return;
        }
        Dag dagInstance = createDagInstance(context);
        addRunningDag(context.getJobKey(), dagInstance);
        submitDagInstance(dagInstance);
    }

    private void addRunningDag(JobKey jobKey, Dag dagInstance) {
        dagInstance.setStatus(Status.RUNNING);
        flowJobExecutorManager.createDagInstance(jobKey, dagInstance);
    }

    private Dag createDagInstance(HodorJobExecutionContext context) {
        NodeBean nodeBean = flowJobExecutorManager.getFlowNodeBean(context.getJobKey());
        Assert.notNull(nodeBean, "not found flow node by job key {}.", context.getJobKey());
        DagCreator dagCreator = new DagCreator(nodeBean);
        Dag dag = dagCreator.create();
        dag.setSchedulerName(context.getSchedulerName());
        return dag;
    }

    private boolean isAlreadyRunningJob(JobKey jobKey) {
        Dag dag = flowJobExecutorManager.getDagInstance(jobKey);
        if (dag == null) {
            return false;
        }
        return !dag.getStatus().isTerminal();
    }

    private void submitDagInstance(Dag dag) {
        FlowJobExecutorManager.getInstance().startDag(dag);
    }

}