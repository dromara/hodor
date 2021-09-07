package org.dromara.hodor.server.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagService;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.NodeLayer;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
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

    public void startDag(Dag dag) {
        dagService.startDag(dag);
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
        }, Status.SUCCESS);

        this.addListener(event -> {
            Node node = event.getValue();
            node.getDag().setStatus(Status.FAILURE);
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

}
