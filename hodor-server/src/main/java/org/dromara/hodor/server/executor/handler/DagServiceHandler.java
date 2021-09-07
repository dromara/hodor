package org.dromara.hodor.server.executor.handler;

import java.util.Date;
import java.util.List;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagService;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.NodeLayer;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.FlowJobExecutorManager;
import org.dromara.hodor.server.executor.JobDispatcher;
import org.dromara.hodor.server.manager.JobExecuteManager;
import org.springframework.stereotype.Service;

/**
 * dag service implement
 *
 * @author tomgs
 * @since 2021/8/30
 */
@Slf4j
@Service
public class DagServiceHandler implements DagService {

    private final JobDispatcher jobDispatcher;

    private final FlowJobExecutorManager flowJobExecutorManager;

    private final JobExecuteManager jobExecuteManager;

    public DagServiceHandler(final JobDispatcher jobDispatcher) {
        this.jobDispatcher = jobDispatcher;
        this.flowJobExecutorManager = FlowJobExecutorManager.getInstance();
        this.jobExecuteManager = JobExecuteManager.getInstance();
    }

    @Override
    public void startDag(Dag dag) {
        Assert.notNull(dag, "dag instance must be not null.");
        dag.getFirstLayer().ifPresent(this::submitLayerNode);
    }

    @Override
    public void markNodeRunning(Node node) {
        log.info("node {} state READY to RUNNING", node);
        node.changeStatus(Status.RUNNING);
        HodorJobExecutionContext hodorJobExecutionContext = getHodorJobExecutionContext(node);
        jobDispatcher.dispatch(hodorJobExecutionContext);
    }

    @Override
    public void markNodeSuccess(Node node) {
        node.markSuccess();
        log.info("Node {} state RUNNING to SUCCESS", node);

        NodeLayer nodeLayer = node.getNodeLayer();
        if (!nodeLayer.getStatus().isTerminal()) {
            return;
        }
        Dag dag = node.getDag();
        int layer = node.getLayer();
        log.info("The {} layer execute SUCCESS.", layer);
        // all layer success
        if (dag.isLastLayer(layer)) {
            dag.setStatus(Status.SUCCESS);
            log.info("DAG {} execute SUCCESS.", dag);
        } else {
            // submit next layer node
            submitLayerNode(dag.getLayer(layer + 1));
        }
        // persist dag instance
    }

    @Override
    public void markNodeKilling(Node node) {
        String groupName = node.getGroupName();
        String nodeName = node.getNodeName();
        // TODO: 实现任务的kill
        JobExecDetail jobExecDetail = jobExecuteManager.queryJobExecDetail(JobKey.of(groupName, nodeName));
        if (!node.getNodeId().equals(jobExecDetail.getId())) {
            throw new IllegalArgumentException("job running");
        }
        if (JobExecuteStatus.isRunning(jobExecDetail.getExecuteStatus())) {
            KillRunningJobResponse killRunningJobResponse = jobExecuteManager.killRunningJob(jobExecDetail);
        }

    }

    @Override
    public void markNodeKilled(Node node) {
        node.markKilled();
        log.info("Node {} is KILLED", node);
    }

    @Override
    public void markNodeFailed(Node node) {
        node.markFailed();
        log.info("Node {} is FAILURE", node);
    }

    @Override
    public void killDag(Dag dag) {
        for (NodeLayer nodeLayer : dag.getNodeLayers()) {
            if (nodeLayer.getStatus().isRunning()) {
                if (nodeLayer.getRunningNodes() == 0) {
                    dag.setStatus(Status.KILLED);
                }
                for (Node node : nodeLayer.getNodes()) {
                    node.kill();
                }
            }
            if (nodeLayer.getStatus().isPreRunState()) {
                for (Node node : nodeLayer.getNodes()) {
                    node.cancel();
                }
            }
        }
    }

    @Override
    public void shutdownAndAwaitTermination() throws InterruptedException {

    }

    private HodorJobExecutionContext getHodorJobExecutionContext(Node node) {
        return new HodorJobExecutionContext(node.getNodeId(), (JobDesc) node.getRawData(),
            node.getDag().getSchedulerName(), new Date());
    }

    public void submitLayerNode(NodeLayer nodeLayer) {
        log.info("Starting execute the {} layer nodes.", nodeLayer.getLayer());
        List<Node> nodes = nodeLayer.getNodes();
        nodeLayer.setStatus(Status.RUNNING);
        nodeLayer.setRunningNodes(nodes.size());
        for (Node node : nodes) {
            flowJobExecutorManager.parallelPublish(Event.create(node, Status.RUNNING));
        }
    }

}
