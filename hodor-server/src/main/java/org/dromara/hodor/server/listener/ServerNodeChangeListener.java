package org.dromara.hodor.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.manager.NodeServerManager;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.ServerNode;

/**
 * server node change listener
 *
 * @author tomgs
 * @since 2020/7/23
 */
@Slf4j
public class ServerNodeChangeListener implements DataChangeListener {

    private final NodeServerManager manager;

    public ServerNodeChangeListener(final NodeServerManager nodeServerManager) {
        this.manager = nodeServerManager;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        // path /scheduler/nodes/${node_ip}
        String nodePath = event.getPath();
        if (!ServerNode.isNodePath(nodePath)) {
            return;
        }

        log.info("ServerNodeChange, eventType: {}, path: {}", event.getType(), nodePath);

        String[] nodePathArr = nodePath.split(ServerNode.PATH_SEPARATOR);
        if (nodePathArr.length != 4) {
            return;
        }

        String nodeIp = nodePathArr[3];
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            manager.addNodeServer(nodeIp);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            manager.removeNodeServer(nodeIp);
        }

    }

}
