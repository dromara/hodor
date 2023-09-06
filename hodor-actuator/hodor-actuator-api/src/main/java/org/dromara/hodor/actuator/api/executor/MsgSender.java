package org.dromara.hodor.actuator.api.executor;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.HodorActuatorApiClient;
import org.dromara.hodor.actuator.api.JobRegister;
import org.dromara.hodor.actuator.api.core.NodeManager;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.node.NodeInfo;

/**
 * 心跳/下线消息发送服务
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class MsgSender {

    private final HodorActuatorApiClient hodorActuatorApiClient;

    private final NodeManager nodeManager;

    private final JobRegister jobRegister;

    public MsgSender(final HodorActuatorApiClient hodorActuatorApiClient, final NodeManager nodeManager, final JobRegister jobRegister) {
        this.hodorActuatorApiClient = hodorActuatorApiClient;
        this.nodeManager = nodeManager;
        this.jobRegister = jobRegister;
    }

    public HeartbeatSender getHeartbeatSender() {
        return new HeartbeatSender();
    }

    public NodeOfflineSender getNodeOfflineSender() {
        return new NodeOfflineSender();
    }

    private ActuatorInfo getActuatorInfo() {
        NodeInfo nodeInfo = nodeManager.getNodeInfo();
        Set<String> groupNames = jobRegister.bindingGroup();
        String clusterName = jobRegister.bindingCluster();

        ActuatorInfo actuatorInfo = new ActuatorInfo();
        actuatorInfo.setName(clusterName);
        actuatorInfo.setNodeInfo(nodeInfo);
        actuatorInfo.setGroupNames(groupNames);
        return actuatorInfo;
    }

    public class HeartbeatSender implements Runnable {
        @Override
        public void run() {
            try {
                ActuatorInfo actuatorInfo = getActuatorInfo();
                hodorActuatorApiClient.sendHeartbeat(actuatorInfo);
            } catch (Exception e) {
                log.warn("HeartbeatSender send message has exception, msg: {}", e.getMessage(), e);
            }
        }
    }

    public class NodeOfflineSender implements Runnable {
        @Override
        public void run() {
            try {
                ActuatorInfo actuatorInfo = getActuatorInfo();
                hodorActuatorApiClient.sendOfflineMsg(actuatorInfo);
            } catch (Exception e) {
                log.warn("HeartbeatSender send message has exception, msg: {}", e.getMessage(), e);
            }
        }
    }

}
