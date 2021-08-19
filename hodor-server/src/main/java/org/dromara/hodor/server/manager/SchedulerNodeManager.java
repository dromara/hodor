package org.dromara.hodor.server.manager;

import com.google.common.collect.Sets;
import java.util.Set;

/**
 * node server manager
 *
 * @author tomgs
 * @since 2020/7/28
 */
public enum SchedulerNodeManager {

    INSTANCE;

    private final Set<String> nodeIps = Sets.newConcurrentHashSet();

    public static SchedulerNodeManager getInstance() {
        return INSTANCE;
    }

    public Set<String> getNodeServers() {
        return nodeIps;
    }

    public String getMasterServer() {
        return "";
    }

    public void addNodeServer(String nodeIp) {
        nodeIps.add(nodeIp);
    }

    public void removeNodeServer(String nodeIp) {
        nodeIps.remove(nodeIp);
    }

    public void clearNodeServer() {
        nodeIps.clear();
    }
}
