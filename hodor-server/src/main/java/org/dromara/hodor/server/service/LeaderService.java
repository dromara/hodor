package org.dromara.hodor.server.service;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.hodor.common.exception.HodorException;
import org.dromara.hodor.common.utils.CopySets;
import org.dromara.hodor.common.utils.LocalHost;
import org.dromara.hodor.core.entity.CopySet;
import org.dromara.hodor.core.entity.HodorMetadata;
import org.dromara.hodor.core.manager.CopySetManager;
import org.dromara.hodor.core.manager.NodeServerManager;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.node.LeaderNode;
import org.dromara.hodor.server.component.Constants;
import org.dromara.hodor.server.listener.ServerNodeChangeListener;
import org.springframework.stereotype.Service;

/**
 * leader service
 *
 * @author tomgs
 * @since 2020/6/30
 */
@Service
@Slf4j
public class LeaderService {

    private final RegisterService registerService;

    private final RegistryCenter registryCenter;

    private final JobInfoService jobInfoService;

    private final NodeServerManager nodeServerManager;

    private final CopySetManager copySetManager;

    public LeaderService(final RegisterService registerService, final JobInfoService jobInfoService) {
        this.registerService = registerService;
        this.registryCenter = registerService.getRegistryCenter();
        this.jobInfoService = jobInfoService;
        this.nodeServerManager = NodeServerManager.getInstance();
        this.copySetManager = CopySetManager.getInstance();
    }

    /**
     * 选举主节点
     */
    public void electLeader(final LeaderExecutionCallback callback) {
        registryCenter.executeInLeader(LeaderNode.LATCH_PATH, () -> {
            if (!hasLeader()) {
                createLeaderNode();
                callback.execute();
            }
        });
    }

    public void electLeader() {
        this.electLeader(() -> {
            log.info("to be leader.");
            registerService.registryServerNodeListener(new ServerNodeChangeListener(nodeServerManager));
            // after to be leader write here
            List<String> currRunningNodes = registerService.getRunningNodes();
            if (CollectionUtils.isEmpty(currRunningNodes)) {
                throw new HodorException("running node count is 0.");
            }

            List<List<String>> copySetIps = CopySets.buildCopySets(currRunningNodes, Constants.REPLICA_COUNT, Constants.SCATTER_WIDTH);
            int setsNum = Math.max(copySetIps.size(), currRunningNodes.size());
            // distribution copySet
            List<CopySet> copySets = Lists.newArrayList();
            for (int i = 0; i < setsNum; i++) {
                int setsIndex = setsNum % copySetIps.size();
                List<String> copySetIp = copySetIps.get(setsIndex);
                CopySet copySet = new CopySet();
                copySet.setId(setsIndex);
                copySet.setServers(copySetIp);
                copySets.add(copySet);
            }

            // get metadata and update
            int jobCount = jobInfoService.queryAssignableJobCount();
            int offset = (int) Math.ceil((double) jobCount / setsNum);
            List<Integer> interval = Lists.newArrayList();
            for (int i = 0; i < setsNum; i++) {
                Integer index = jobInfoService.queryJobHashIdByOffset(offset * i);
                interval.add(index);
            }
            for (int i = 0; i < interval.size(); i++) {
                CopySet copySet = copySets.get(i);
                if (i == interval.size() - 1) {
                    copySet.setDataInterval(Lists.newArrayList(interval.get(i)));
                } else {
                    copySet.setDataInterval(Lists.newArrayList(interval.get(i), interval.get(i + 1)));
                }
                copySet.setLeader(copySetManager.selectLeaderCopySet(copySet));
            }

            final HodorMetadata metadata = HodorMetadata.builder()
                .nodes(currRunningNodes)
                .interval(interval)
                .copySets(copySets)
                .build();
            registerService.createMetadata(metadata);
        });
    }

    /**
     * 创建主节点
     */
    public void createLeaderNode() {
        registryCenter.createEphemeral(LeaderNode.ACTIVE_PATH, LocalHost.getIp());
    }

    /**
     *是否存在主节点
     */
    public boolean hasLeader() {
        return registryCenter.checkExists(LeaderNode.ACTIVE_PATH);
    }

    /**
     * 当期节点是否为主节点
     */
    public boolean isLeader() {
        return !LocalHost.getIp().equals(registryCenter.get(LeaderNode.ACTIVE_PATH));
    }

    public void stop() {
        nodeServerManager.clearNodeServer();
        copySetManager.clearCopySet();
    }
}
