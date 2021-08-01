package org.dromara.hodor.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.server.manager.WorkerNodeManager;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.ServerNode;

/**
 * worker node listener
 *
 * @author tomgs
 * @since 2020/11/27
 */
@Slf4j
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

        log.info("WorkerNodeChange, eventType: {}, path: {}", event.getType(), workerPath);

        // path: /worker/${groupName}/${endpoint}
        String[] workerPathArr = workerPath.split(ServerNode.PATH_SEPARATOR);
        if (workerPathArr.length != 4) {
            return;
        }

        String groupName = workerPathArr[2];
        String nodeEndpoint = workerPathArr[3];
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            workerNodeManager.addWorkerNode(groupName, nodeEndpoint);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            workerNodeManager.removeWorkerNode(groupName, nodeEndpoint);
        }

    }

}
