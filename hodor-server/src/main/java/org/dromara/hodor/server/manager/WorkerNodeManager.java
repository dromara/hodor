package org.dromara.hodor.server.manager;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * node server manager
 *
 * @author tomgs
 * @since 2020/7/28
 */
public enum WorkerNodeManager {

    INSTANCE;

    private final Map<String, Set<String>> workerNodes = Maps.newConcurrentMap();

    public static WorkerNodeManager getInstance() {
        return INSTANCE;
    }

    public Set<String> getNodeServers(final String groupName) {
        return workerNodes.get(groupName);
    }

    public void addWorkerNodes(String groupName, Set<String> nodes) {
        workerNodes.put(groupName, nodes);
    }

    public void removeWorkerNodes(String groupName) {
        workerNodes.remove(groupName);
    }

    public void clearWorkerNodes() {
        workerNodes.clear();
    }

    public void addWorkerNode(String groupName, String nodeEndpoint) {
        Set<String> nodes = workerNodes.get(groupName);
        if (nodes == null) {
            nodes = Sets.newConcurrentHashSet();
        }
        nodes.add(nodeEndpoint);
        workerNodes.put(groupName, nodes);
    }

    public void removeWorkerNode(String groupName, String nodeEndpoint) {
        Set<String> nodes = workerNodes.get(groupName);
        if (nodes == null) {
            return;
        }
        nodes.remove(nodeEndpoint);
    }
}
