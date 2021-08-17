package org.dromara.hodor.server.manager;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.loadbalance.LoadBalance;
import org.dromara.hodor.common.loadbalance.LoadBalanceEnum;
import org.dromara.hodor.common.loadbalance.LoadBalanceFactory;
import org.dromara.hodor.model.node.NodeInfo;

/**
 *  actuator node manager
 *
 * @author tomgs
 * @version 2021/8/1 1.0
 */
@Slf4j
public class ActuatorNodeManager {

    private static final ActuatorNodeManager INSTANCE = new ActuatorNodeManager();

    // endpoint -> nodeInfo
    private final Map<String, NodeInfo> actuatorNodes = Maps.newConcurrentMap();

    // endpoint -> heartbeat timestamp
    private final Map<String, Long> actuatorNodeLastHeartbeat = Maps.newConcurrentMap();

    // groupName -> endpoint set
    private final Map<String, Set<String>> actuatorEndpoints = Maps.newConcurrentMap();

    private static final int HEARTBEAT_THRESHOLD = 30_000;

    private ScheduledExecutorService cleanSchedule;

    private ActuatorNodeManager() {

    }

    public static ActuatorNodeManager getInstance() {
        return INSTANCE;
    }

    public void startOfflineActuatorClean() {
        this.cleanSchedule = Executors.newSingleThreadScheduledExecutor(HodorThreadFactory.create("offline-actuator-cleaner", false));
        this.cleanSchedule.scheduleWithFixedDelay(this::offlineActuatorClean, 30,60, TimeUnit.SECONDS);
    }

    public void offlineActuatorClean() {
        log.info("offline actuator clean ...");
        actuatorNodeLastHeartbeat.entrySet().removeIf(entry -> {
            Long lastHeartbeat = entry.getValue();
            if (heartbeatThresholdExceedCheck(lastHeartbeat)) {
                String endpoint = entry.getKey();
                removeNode(endpoint);
                return true;
            }
            return false;
        });
    }

    private void removeNode(String endpoint) {
        actuatorNodes.remove(endpoint);
        actuatorEndpoints.values().forEach(endpoints -> endpoints.removeIf(e -> e.equals(endpoint)));
        actuatorNodeLastHeartbeat.remove(endpoint);
    }

    public void addActuatorEndpoint(String groupName, String nodeEndpoint) {
        Set<String> endpoints = actuatorEndpoints.computeIfAbsent(groupName, e -> Sets.newConcurrentHashSet());
        endpoints.add(nodeEndpoint);
    }

    public void removeActuatorGroup(String groupName, String nodeEndpoint) {
        Optional.ofNullable(actuatorEndpoints.get(groupName)).ifPresent(endpoints -> endpoints.removeIf(endpoint -> endpoint.equals(nodeEndpoint)));
    }

    public Set<String> getActuatorEndpointsByGroupName(final String groupName) {
        return actuatorEndpoints.getOrDefault(groupName, Sets.newConcurrentHashSet());
    }

    public boolean isOffline(String endpoint) {
        Long lastHeartbeat = actuatorNodeLastHeartbeat.getOrDefault(endpoint, 0L);
        return heartbeatThresholdExceedCheck(lastHeartbeat);
    }

    private boolean heartbeatThresholdExceedCheck(Long lastHeartbeat) {
        return System.currentTimeMillis() - lastHeartbeat > HEARTBEAT_THRESHOLD;
    }

    public void clearActuatorNodes() {
        actuatorEndpoints.clear();
        actuatorNodeLastHeartbeat.clear();
    }

    public void stopOfflineActuatorClean() {
        if (cleanSchedule != null) {
            cleanSchedule.shutdown();
        }
    }

    public List<Host> getAvailableHosts(String groupName) {
        List<String> allWorkNodes = Lists.newArrayList(getActuatorEndpointsByGroupName(groupName));
        List<Host> hosts = allWorkNodes.stream()
            .filter(endpoint -> !isOffline(endpoint))
            .map(Host::of)
            .collect(Collectors.toList());

        Assert.notEmpty(hosts, "The group [{}] has no available nodes.", groupName);

        LoadBalance loadBalance = LoadBalanceFactory.getLoadBalance(LoadBalanceEnum.RANDOM.name());
        Host selected = loadBalance.select(hosts);
        hosts.remove(selected);
        hosts.add(selected);
        return hosts;
    }

    public void addActuatorNode(String nodeEndpoint, NodeInfo nodeInfo) {
        actuatorNodes.put(nodeEndpoint, nodeInfo);
    }

    public NodeInfo getActuatorNode(String nodeEndpoint) {
        return actuatorNodes.getOrDefault(nodeEndpoint, new NodeInfo());
    }

    public void removeActuatorNode(String nodeEndpoint) {
        actuatorNodes.remove(nodeEndpoint);
    }

    public void refreshActuatorEndpointHeartbeat(String nodeEndpoint, String heartbeat) {
        actuatorNodeLastHeartbeat.put(nodeEndpoint, Long.valueOf(heartbeat));
    }

}
