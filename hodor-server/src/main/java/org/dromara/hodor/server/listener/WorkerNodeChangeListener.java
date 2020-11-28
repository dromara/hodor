package org.dromara.hodor.server.listener;

import com.google.common.collect.Sets;
import org.dromara.hodor.core.manager.WorkerNodeManager;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.ServerNode;

/**
 * worker node listener
 *
 * @author tomgs
 * @since 2020/11/27
 */
public class WorkerNodeChangeListener implements DataChangeListener {

    private final WorkerNodeManager workerNodeManager;

    public WorkerNodeChangeListener(final WorkerNodeManager workerNodeManager) {
        this.workerNodeManager = workerNodeManager;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        String workerPath = event.getPath();
        if (!ServerNode.isWorkerPath(workerPath)) {
            return;
        }

        String groupName = "";
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            workerNodeManager.addWorkerNodes(groupName, Sets.newConcurrentHashSet());
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            workerNodeManager.removeWorkerNodes(groupName);
        }

    }

}
