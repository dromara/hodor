package org.dromara.hodor.core.manager;

import com.google.common.collect.Sets;
import java.util.Set;

/**
 * node server manager
 *
 * @author tomgs
 * @since 2020/7/28
 */
public enum NodeServerManager {
    INSTANCE;

    private final Set<String> nodeIps = Sets.newConcurrentHashSet();

    public static NodeServerManager getInstance() {
        return INSTANCE;
    }

    public Set<String> getNodeServers() {
        return nodeIps;
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
