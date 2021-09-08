package org.dromara.hodor.server.executor;

import cn.hutool.core.lang.Assert;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagService;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.NodeLayer;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.storage.cache.CacheSource;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;
import org.dromara.hodor.core.dag.NodeBean;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.server.ServiceProvider;

/**
 * FlowJobExecutorManager
 *
 * @author tomgs
 * @since 2021/9/2
 */
@Slf4j
public class FlowJobExecutorManager extends AbstractAsyncEventPublisher<Node> {

    private static volatile FlowJobExecutorManager INSTANCE;

    private final DagService dagService;

    private final CacheSource<JobKey, Dag> dagCacheSource;

    private final CacheSource<JobKey, NodeBean> flowNodeCacheSource;

    private FlowJobExecutorManager() {
        this.dagService = ServiceProvider.getInstance().getBean(DagService.class);
        HodorCacheSource hodorCacheSource = ServiceProvider.getInstance().getBean(HodorCacheSource.class);
        this.dagCacheSource = hodorCacheSource.getCacheSource("dag_instance");
        this.flowNodeCacheSource = hodorCacheSource.getCacheSource("flow_node");
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

    public void startDag(Dag dag) {
        Assert.notNull(dag, "dag instance must be not null.");
        dag.getFirstLayer().ifPresent(this::submitLayerNode);
    }

    public void submitLayerNode(NodeLayer nodeLayer) {
        log.info("Starting execute the {} layer nodes.", nodeLayer.getLayer());
        List<Node> nodes = nodeLayer.getNodes();
        nodeLayer.setStatus(Status.RUNNING);
        nodeLayer.setRunningNodes(nodes.size());
        for (Node node : nodes) {
            parallelPublish(Event.create(node, Status.RUNNING));
        }
    }

    @Override
    public void registerListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            if (node.getStatus() != Status.READY) {
                log.warn("node {} status {} is not ready state can't to running.", node.getNodeKeyName(), node.getStatus());
                return;
            }
            dagService.markNodeRunning(node);
        }, Status.RUNNING);

        this.addListener(event -> {
            Node node = event.getValue();
            if (!node.getStatus().isRunning()) {
                log.warn("node {} status {} is not running state can't to success.", node.getNodeKeyName(), node.getStatus());
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
                dag.setStatus(Status.SUCCESS);
                log.info("DAG {} execute SUCCESS.", dag);
            } else {
                // submit next layer node
                submitLayerNode(dag.getLayer(layer + 1));
            }
        }, Status.SUCCESS);

        this.addListener(event -> {
            Node node = event.getValue();
            dagService.markNodeFailed(node);
        }, Status.FAILURE);

        this.addListener(event -> {
            Node node = event.getValue();
            if (!node.getStatus().isRunning()) {
                log.warn("node {} status {} is not running state can't to killing.", node.getNodeKeyName(), node.getStatus());
                return;
            }
            dagService.markNodeKilling(node);
        }, Status.KILLING);

        this.addListener(event -> {
            Node node = event.getValue();
            Dag dag = node.getDag();
            NodeLayer nodeLayer = node.getCurrentNodeLayer();
            dagService.markNodeKilled(node);
            if (nodeLayer.getRunningNodes() == 0) {
                dag.setStatus(Status.KILLED);
            }
        }, Status.KILLED);
    }

    public void putDagInstance(JobKey jobKey, Dag dagInstance) {
        dagCacheSource.put(jobKey, dagInstance);
    }

    public NodeBean getFlowNode(JobKey jobKey) {
        return flowNodeCacheSource.get(jobKey);
    }

    public Dag getDagInstance(JobKey jobKey) {
        return dagCacheSource.get(jobKey);
    }

    public Node getNodeInstance(JobKey jobKey) {
        Dag dagInstance = getDagInstance(jobKey);
        Assert.notNull(dagInstance, "not found dag instance by job key {}.", jobKey);
        return dagInstance.getNode(jobKey.getGroupName(), jobKey.getJobName());
    }

    public void changeNodeStatus(JobKey jobKey, Status status) {
        Node node = getNodeInstance(jobKey);
        Assert.notNull("node {} must be not null.", node.getNodeKeyName());
        publish(Event.create(node, status));
    }

}
