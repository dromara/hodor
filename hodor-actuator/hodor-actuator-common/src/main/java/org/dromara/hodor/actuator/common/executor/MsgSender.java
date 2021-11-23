package org.dromara.hodor.actuator.common.executor;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.common.HodorApiClient;
import org.dromara.hodor.actuator.common.core.NodeManager;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.node.NodeInfo;

/**
 * 心跳/下线消息发送服务
 *
 * @author tomgs
 * @since 2021/1/7
 */
@Slf4j
public class MsgSender {

    private final HodorApiClient hodorApiClient;

    private final NodeManager nodeManager;

    public MsgSender(final HodorApiClient hodorApiClient, final NodeManager nodeManager) {
        this.hodorApiClient = hodorApiClient;
        this.nodeManager = nodeManager;
    }

    public HeartbeatSender getHeartbeatSender() {
        return new HeartbeatSender();
    }

    public NodeOfflineSender getNodeOfflineSender() {
        return new NodeOfflineSender();
    }

    private ActuatorInfo getActuatorInfo() {
        NodeInfo nodeInfo = nodeManager.getNodeInfo();
        // TODO: fix this
        Set<String> groupNames = null;//jobRegistrar.getGroupNames();
        ActuatorInfo actuatorInfo = new ActuatorInfo();
        actuatorInfo.setNodeInfo(nodeInfo);
        actuatorInfo.setGroupNames(groupNames);
        return actuatorInfo;
    }

    public class HeartbeatSender implements Runnable {
        @Override
        public void run() {
            try {
                ActuatorInfo actuatorInfo = getActuatorInfo();
                hodorApiClient.sendHeartbeat(actuatorInfo);
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
                hodorApiClient.sendOfflineMsg(actuatorInfo);
            } catch (Exception e) {
                log.warn("HeartbeatSender send message has exception, msg: {}", e.getMessage(), e);
            }
        }
    }

}
