package org.dromara.hodor.server.executor;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagService;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.NodeLayer;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
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

    private FlowJobExecutorManager() {
        this.dagService = ServiceProvider.getInstance().getBean(DagService.class);
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

    public void submitLayerNode(NodeLayer nodeLayer) {
        List<Node> nodes = nodeLayer.getNodes();
        nodeLayer.setStatus(Status.RUNNING);
        nodeLayer.setRunningNodes(nodes.size());
        for (Node node : nodes) {
            FlowJobExecutorManager.getInstance().publish(Event.create(node, Status.RUNNING));
        }
    }

    @Override
    public void registerListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            if (node.getStatus() != Status.READY) {
                log.error("node {} status {} is not ready.", node.getName(), node.getStatus());
                return;
            }
            dagService.markNodeRunning(node);
            HodorJobExecutionContext hodorJobExecutionContext = new HodorJobExecutionContext(node.getNodeId(), null, null, null);
            JobDispatcher.getInstance().dispatch(hodorJobExecutionContext);
        }, Status.RUNNING);

        this.addListener(event -> {
            Node node = event.getValue();
            if (!node.getStatus().isRunning()) {
                log.error("node {} status {} is not running.", node.getName(), node.getStatus());
                return;
            }
            dagService.markNodeSuccess(node);

            int layer = node.getLayer();
            Dag dag = node.getDag();
            NodeLayer nodeLayer = dag.getLayer(layer);
            if (!nodeLayer.getStatus().isTerminal()) {
                return;
            }
            // all layer success
            if (dag.isLastLayer(layer)) {
                dag.setStatus(Status.SUCCESS);
            } else {
                // submit next layer node
                submitLayerNode(dag.getLayer(layer + 1));
            }
        }, Status.SUCCESS);

        this.addListener(event -> {
            Node node = event.getValue();
            node.getDag().setStatus(Status.FAILURE);
        }, Status.FAILURE);

        this.addListener(event -> {
            Node node = event.getValue();
            node.getDag().setStatus(Status.CANCELED);
        }, Status.CANCELED);

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

}
