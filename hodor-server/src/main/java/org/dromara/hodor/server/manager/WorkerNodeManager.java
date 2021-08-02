package org.dromara.hodor.server.manager;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.loadbalance.LoadBalance;
import org.dromara.hodor.common.loadbalance.LoadBalanceEnum;
import org.dromara.hodor.common.loadbalance.LoadBalanceFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Host> getAvailableHosts(String groupName) {
        List<String> allWorkNodes = Lists.newArrayList(getNodeServers(groupName));
        List<Host> hosts = allWorkNodes.stream().map(Host::of).collect(Collectors.toList());

        Assert.notEmpty(hosts, "The group [{}] has no available nodes.", groupName);

        LoadBalance loadBalance = LoadBalanceFactory.getLoadBalance(LoadBalanceEnum.RANDOM.name());
        Host selected = loadBalance.select(hosts);
        hosts.remove(selected);
        hosts.add(selected);
        return hosts;
    }
}
