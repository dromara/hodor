package org.dromara.hodor.server.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.TimeUtil;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.ActuatorNode;
import org.dromara.hodor.server.manager.ActuatorNodeManager;

/**
 * actuator node listener
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class ActuatorNodeChangeListener implements DataChangeListener {

    private final ActuatorNodeManager actuatorNodeManager;

    public ActuatorNodeChangeListener(final ActuatorNodeManager actuatorNodeManager) {
        this.actuatorNodeManager = actuatorNodeManager;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        String actuatorPath = event.getPath();
        if (ActuatorNode.isClusterPath(actuatorPath)) {
            changeActuatorClusterData(event, actuatorPath);
            return;
        }

        if (ActuatorNode.isBindingPath(actuatorPath)) {
            changeActuatorBindingData(event, actuatorPath);
        }
    }

    private void changeActuatorBindingData(DataChangeEvent event, String actuatorPath) {
        log.debug("ActuatorBindingChange, eventType: {}, path: {}", event.getType(), actuatorPath);
        // path: /actuator/binding/${clusterName}/${groupName}
        List<String> actuatorClusterPath = StringUtils.splitPath(actuatorPath);
        if (actuatorClusterPath.size() != 4) {
            return;
        }
        String clusterName = actuatorClusterPath.get(2);
        String groupName = actuatorClusterPath.get(3);
        if (event.getType() == DataChangeEvent.Type.NODE_UPDATED || event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            actuatorNodeManager.addClusterGroupEntry(clusterName, groupName);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            actuatorNodeManager.removeClusterGroupEntry(clusterName, groupName);
        }
    }

    private void changeActuatorClusterData(DataChangeEvent event, String actuatorPath) {
        log.debug("ActuatorClusterChange, eventType: {}, path: {}", event.getType(), actuatorPath);
        // path: /actuator/clusters/${clusterName}/${endpoint} -> nodeInfo
        List<String> actuatorClusterPath = StringUtils.splitPath(actuatorPath);
        if (actuatorClusterPath.size() != 4) {
            return;
        }
        String clusterName = actuatorClusterPath.get(2);
        String nodeEndpoint = actuatorClusterPath.get(3);
        final NodeInfo nodeInfo = Jsons.toBean(StringUtils.decodeString(event.getData()), NodeInfo.class);
        if (event.getType() == DataChangeEvent.Type.NODE_UPDATED || event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            final long lastHeartbeat = TimeUtil.currentTimeMillis();
            actuatorNodeManager.addActuatorClusterEndpoint(clusterName, nodeEndpoint);
            actuatorNodeManager.addActuatorClusterNodeInfo(nodeEndpoint, nodeInfo, lastHeartbeat);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            actuatorNodeManager.removeActuatorClusterEndpoint(clusterName, nodeEndpoint);
            actuatorNodeManager.removeActuatorClusterNodeInfo(nodeEndpoint);
        }
    }

}
