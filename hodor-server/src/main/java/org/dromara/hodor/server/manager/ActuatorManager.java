package org.dromara.hodor.server.manager;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.node.NodeInfo;

/**
 *  actuator manager
 *
 * @author tomgs
 * @version 2021/8/1 1.0
 */
@Slf4j
public class ActuatorManager {

    private static final ActuatorManager INSTANCE = new ActuatorManager();

    // TODO: 下面存储需要改成分布式存储
    private final Map<String, Set<NodeInfo>> actuatorNodes = Maps.newConcurrentMap();

    private final Map<String, Set<String>> actuatorEndpoints = Maps.newConcurrentMap();

    private final Map<NodeInfo, Long> actuatorNodeLastHeartbeat = Maps.newConcurrentMap();

    private static final int HEARTBEAT_THRESHOLD = 5000;

    private ActuatorManager() {
        ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor(HodorThreadFactory.create("offline-actuator-cleaner", false));
        schedule.scheduleAtFixedRate(this::offlineActuatorClean, 30,60, TimeUnit.SECONDS);
    }

    public static ActuatorManager getInstance() {
        return INSTANCE;
    }

    public void offlineActuatorClean() {
        log.info("offline actuator clean ...");
        actuatorNodeLastHeartbeat.entrySet().removeIf(entry -> {
            Long lastHeartbeat = entry.getValue();
            if (heartbeatThresholdExceedCheck(lastHeartbeat)) {
                NodeInfo nodeInfo = entry.getKey();
                removeNodeInfo(nodeInfo);
                return true;
            }
            return false;
        });
    }

    public void addActuator(final ActuatorInfo actuatorInfo) {
        Preconditions.checkNotNull(actuatorInfo.getNodeInfo(), "actuator node info must be not null.");
        Preconditions.checkNotNull(actuatorInfo.getGroupNames(), "actuator group names must be not null.");
        actuatorNodeLastHeartbeat.put(actuatorInfo.getNodeInfo(), actuatorInfo.getLastHeartbeat());
        actuatorInfo.getGroupNames().forEach(groupName -> {
            Set<NodeInfo> nodeInfos = actuatorNodes.computeIfAbsent(groupName, e -> Sets.newConcurrentHashSet());
            nodeInfos.add(actuatorInfo.getNodeInfo());

            Set<String> endpoints = actuatorEndpoints.computeIfAbsent(groupName, e -> Sets.newConcurrentHashSet());
            endpoints.add(buildActuatorEndpoint(actuatorInfo.getNodeInfo()));
        });
    }

    public Set<NodeInfo> getActuatorsByGroupName(final String groupName) {
        return actuatorNodes.getOrDefault(groupName, Sets.newConcurrentHashSet());
    }

    public Set<String> getActuatorEndpointsByGroupName(final String groupName) {
        return actuatorEndpoints.getOrDefault(groupName, Sets.newConcurrentHashSet());
    }

    public void removeActuator(final ActuatorInfo actuatorInfo) {
        removeNodeInfo(actuatorInfo.getNodeInfo());
    }

    public void removeNodeInfo(final NodeInfo actuatorNodeInfo) {
        for (Set<NodeInfo> nodeInfos : actuatorNodes.values()) {
            nodeInfos.removeIf(nodeInfo -> nodeInfo.equals(actuatorNodeInfo));
        }
        for (Set<String> endpoints : actuatorEndpoints.values()) {
            endpoints.removeIf(endpoint -> endpoint.equals(buildActuatorEndpoint(actuatorNodeInfo)));
        }
    }

    public boolean isOffline(NodeInfo nodeInfo) {
        Long lastHeartbeat = actuatorNodeLastHeartbeat.get(nodeInfo);
        return heartbeatThresholdExceedCheck(lastHeartbeat);
    }

    private String buildActuatorEndpoint(final NodeInfo nodeInfo) {
        return nodeInfo.getIp() + ":" + nodeInfo.getPort();
    }

    private boolean heartbeatThresholdExceedCheck(Long lastHeartbeat) {
        return System.currentTimeMillis() - lastHeartbeat > HEARTBEAT_THRESHOLD;
    }

}
