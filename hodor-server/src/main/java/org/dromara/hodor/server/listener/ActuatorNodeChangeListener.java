package org.dromara.hodor.server.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.HodorEventListener;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.ActuatorNode;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.service.RegistryService;

/**
 * actuator node listener
 *
 * @author tomgs
 * @since 2020/11/27
 */
@Slf4j
public class ActuatorNodeChangeListener implements DataChangeListener, HodorEventListener<ActuatorInfo> {

    private final ActuatorNodeManager actuatorNodeManager;

    private final RegistryService registryService;

    public ActuatorNodeChangeListener(final ActuatorNodeManager actuatorNodeManager, final RegistryService registryService) {
        this.actuatorNodeManager = actuatorNodeManager;
        this.registryService = registryService;
        this.actuatorNodeManager.addListener(this);
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        String actuatorPath = event.getPath();
        if (ActuatorNode.isNodePath(actuatorPath)) {
            changeActuatorNodeData(event, actuatorPath);
            return;
        }

        if (ActuatorNode.isGroupPath(actuatorPath)) {
            changeActuatorGroupData(event, actuatorPath);
        }
    }

    private void changeActuatorGroupData(DataChangeEvent event, String actuatorPath) {
        log.debug("ActuatorGroupChange, eventType: {}, path: {}", event.getType(), actuatorPath);
        // path: /actuator/groups/${groupName}/${endpoint}
        List<String> actuatorGroupPath = StringUtils.splitPath(actuatorPath);
        if (actuatorGroupPath.size() != 4) {
            return;
        }

        String groupName = actuatorGroupPath.get(2);
        String nodeEndpoint = actuatorGroupPath.get(3);
        if (event.getType() == DataChangeEvent.Type.NODE_UPDATED || event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            long lastHeartbeat = Long.parseLong(StringUtils.decodeString(event.getData()));
            actuatorNodeManager.addActuatorNodeInfo(groupName, nodeEndpoint, lastHeartbeat);
            actuatorNodeManager.addActuatorEndpoint(groupName, nodeEndpoint);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            actuatorNodeManager.removeActuatorGroup(groupName, nodeEndpoint);
        }
    }

    private void changeActuatorNodeData(DataChangeEvent event, String actuatorPath) {
        log.debug("ActuatorNodeChange, eventType: {}, path: {}", event.getType(), actuatorPath);
        // path: /actuator/nodes/${endpoint}
        List<String> actuatorNodePath = StringUtils.splitPath(actuatorPath);
        if (actuatorNodePath.size() != 3) {
            return;
        }

        String nodeEndpoint = actuatorNodePath.get(2);
        NodeInfo nodeInfo = event.getData() == null ? new NodeInfo() : SerializeUtils.deserialize(event.getData(), NodeInfo.class);

        if (event.getType() == DataChangeEvent.Type.NODE_UPDATED || event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            actuatorNodeManager.addActuatorNode(nodeEndpoint, nodeInfo);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            actuatorNodeManager.removeActuatorNode(nodeEndpoint);
        }
    }

    @Override
    public void onEvent(Event<ActuatorInfo> event) {
        ActuatorInfo actuatorInfo = event.getValue();
        registryService.removeActuator(actuatorInfo);
    }

}
