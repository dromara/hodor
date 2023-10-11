package org.dromara.hodor.server.executor;

import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.NodeLayer;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.core.dag.FlowData;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.executor.dispatch.JobDispatcher;
import org.dromara.hodor.server.executor.handler.DagService;
import org.dromara.hodor.server.executor.handler.HodorFlowJobRequestHandler;
import org.dromara.hodor.server.manager.JobExecuteManager;

/**
 * FlowJobExecutorManager
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class FlowJobExecutorManager extends AbstractAsyncEventPublisher<Node> {

    private static volatile FlowJobExecutorManager INSTANCE;

    private final DagService dagService;

    private final JobDispatcher flowJobDispatcher;

    private FlowJobExecutorManager() {
        this.dagService = ServiceProvider.getInstance().getBean(DagService.class);
        this.flowJobDispatcher = new JobDispatcher(new HodorFlowJobRequestHandler());
    }

    public static FlowJobExecutorManager getInstance() {
        if (INSTANCE == null) {
            synchronized (FlowJobExecutorManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlowJobExecutorManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 启动Dag
     *
     * @param dag Dag实例
     */
    public void startDag(Dag dag) {
        Assert.notNull(dag, "dag instance must be not null.");
        dag.getFirstLayer().ifPresent(this::submitLayerNode);
    }

    /**
     * 停止Dag
     *
     * @param dag Dag实例
     */
    public void killDag(Dag dag) {
        Assert.notNull(dag, "dag instance must be not null.");
        for (NodeLayer nodeLayer : dag.getNodeLayers()) {
            if (nodeLayer.getStatus().isRunning()) {
                for (Node node : nodeLayer.getNodes()) {
                    parallelPublish(Event.create(node, Status.KILLING));
                }
            }
            if (nodeLayer.getStatus().isPreRunState()) {
                for (Node node : nodeLayer.getNodes()) {
                    dagService.markNodeCanceled(node);
                }
            }
        }
    }

    /**
     * 更新Dag状态
     *
     * @param dag Dag实例
     */
    public void updateDagStatus(Dag dag) {
        Assert.notNull(dag, "dag instance must be not null.");
        dagService.updateDagStatus(dag);
    }

    public void submitLayerNode(NodeLayer nodeLayer) {
        log.info("Starting execute the {} layer nodes.", nodeLayer.getLayer());
        List<Node> nodes = nodeLayer.getNodes();
        nodeLayer.setStatus(Status.RUNNING);
        nodeLayer.setRunningNodeNums(nodes.size());
        for (Node node : nodes) {
            parallelPublish(Event.create(node, Status.RUNNING));
        }
    }

    public void createDagInstance(JobKey rootJobKey, Dag dagInstance) {
        dagService.createDagInstance(rootJobKey, dagInstance);
    }

    public Dag getDagInstance(JobKey rootJobKey) {
        return dagService.getDagInstance(rootJobKey);
    }

    public void putFlowData(JobKey rootJobKey, FlowData flowData) {
        dagService.putFlowData(rootJobKey, flowData);
    }

    public FlowData getFlowData(JobKey rootJobKey) {
        return dagService.getFlowData(rootJobKey);
    }

    public void changeNodeStatus(Node node, Status status) {
        Assert.notNull("node {} must be not null.", node.getNodeName());
        publish(Event.create(node, status));
    }

    @Override
    public void registryListener() {
        registerRunningNodeListener();
        registerSuccessNodeListener();
        registerFailureNodeListener();
        registerKillingNodeListener();
        registerKilledNodeListener();
    }

    private void registerRunningNodeListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            if (node.getStatus() != Status.READY) {
                log.warn("node {}#{} status {} is not ready state can't to running.", node.getNodeName(), node.getNodeId(), node.getStatus());
                return;
            }

            dagService.markNodeRunning(node);

            HodorJobExecutionContext hodorJobExecutionContext = getHodorJobExecutionContext(node);
            flowJobDispatcher.dispatch(hodorJobExecutionContext);
        }, Status.RUNNING);
    }

    private void registerSuccessNodeListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            if (!node.getStatus().isRunning()) {
                log.warn("node {} status {} is not running state can't to success.", node.getNodeName(), node.getStatus());
                return;
            }
            dagService.markNodeSuccess(node);
            NodeLayer nodeLayer = node.getNodeLayer();
            if (!nodeLayer.getStatus().isTerminal()) {
                return;
            }
            Dag dag = node.getDag();
            int layer = node.getLayer();
            log.info("The {} layer execute SUCCESS.", layer);
            // all layer success
            if (dag.isLastLayer(layer)) {
                log.info("DAG {} execute FINISHED.", dag);
                updateDagStatus(dag);
            } else {
                // submit next layer node
                submitLayerNode(dag.getLayer(layer + 1));
            }
        }, Status.SUCCESS);
    }

    private void registerFailureNodeListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            NodeLayer nodeLayer = node.getCurrentNodeLayer();
            nodeLayer.setStatus(Status.FAILURE);
            List<Node> runningNodes = nodeLayer.getRunningNodes();
            runningNodes.forEach(runningNode -> publish(Event.create(runningNode, Status.KILLING)));
            dagService.markNodeFailed(node);
            //dagService.updateDagStatus(node.getDag());
        }, Status.FAILURE);
    }

    private void registerKillingNodeListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            if (!node.getStatus().isRunning()) {
                log.warn("node {} status {} is not running state can't to killing.", node.getNodeName(), node.getStatus());
                return;
            }
            dagService.markNodeKilling(node);

            String groupName = node.getGroupName();
            String nodeName = node.getNodeName();
            JobExecDetail jobExecDetail = JobExecuteManager.getInstance().queryJobExecDetail(JobKey.of(groupName, nodeName));
            if (!node.getNodeId().equals(jobExecDetail.getId())) {
                throw new IllegalArgumentException(StringUtils.format("node id is {} but job instance id is {}, they don't matched.", node.getNodeId(), jobExecDetail.getId()));
            }
            if (JobExecuteStatus.isRunning(jobExecDetail.getExecuteStatus())) {
                KillRunningJobResponse killRunningJobResponse = JobExecuteManager.getInstance().killRunningJob(jobExecDetail);
                if (JobExecuteStatus.isKilled(killRunningJobResponse.getStatus())) {
                    publish(Event.create(node, Status.KILLED));
                }
            }
        }, Status.KILLING);
    }

    private void registerKilledNodeListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            Dag dag = node.getDag();
            NodeLayer nodeLayer = node.getCurrentNodeLayer();
            dagService.markNodeKilled(node);
            if (nodeLayer.getRunningNodeNums() == 0) {
                if (dag.getStatus().isRunning()) {
                    dag.setStatus(Status.KILLED);
                }
                dag.changeStatus(nodeLayer.getStatus());
                updateDagStatus(dag);
            }
        }, Status.KILLED);
    }

    private HodorJobExecutionContext getHodorJobExecutionContext(Node node) {
        return new HodorJobExecutionContext(0, node.getNodeId(),
            JobKey.of(node.getDag().getName()),
            (JobDesc) node.getRawData(),
            node.getDag().getSchedulerName(),
            new Date());
    }

}
