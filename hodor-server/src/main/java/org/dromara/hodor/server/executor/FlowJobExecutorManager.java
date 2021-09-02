package org.dromara.hodor.server.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.DagService;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.server.ServiceProvider;

/**
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

    @Override
    public void registerListener() {
        this.addListener(event -> {
            Node node = event.getValue();
            if (node.getStatus() != Status.READY) {
                log.warn("node {} status is not ready.", node.getName());
                return;
            }
            dagService.markNodeRunning(node);
        }, Status.RUNNING);

        this.addListener(event -> {

        }, Status.SUCCESS);

        this.addListener(event -> {

        }, Status.FAILURE);

        this.addListener(event -> {

        }, Status.CANCELED);

        this.addListener(event -> {

        }, Status.KILLED);
    }

    private void assertRunningOrKilling(Status status) {
        assert (status == Status.RUNNING || status == Status.KILLING);
    }
}
