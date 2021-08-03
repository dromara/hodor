package org.dromara.hodor.server.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.manager.SchedulerNodeManager;

/**
 * server node change listener
 *
 * @author tomgs
 * @since 2020/7/23
 */
@Slf4j
public class SchedulerNodeChangeListener implements DataChangeListener {

    private final SchedulerNodeManager manager;

    public SchedulerNodeChangeListener(final SchedulerNodeManager schedulerNodeManager) {
        this.manager = schedulerNodeManager;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        // path /scheduler/nodes/${node_ip}
        String nodePath = event.getPath();
        if (!SchedulerNode.isNodePath(nodePath)) {
            return;
        }

        log.info("ServerNodeChange, eventType: {}, path: {}", event.getType(), nodePath);

        List<String> schedulerNodePath = StringUtils.splitPath(nodePath);
        if (schedulerNodePath.size() != 3) {
            return;
        }

        String nodeIp = schedulerNodePath.get(2);
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            manager.addNodeServer(nodeIp);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            manager.removeNodeServer(nodeIp);
        }

    }

}
