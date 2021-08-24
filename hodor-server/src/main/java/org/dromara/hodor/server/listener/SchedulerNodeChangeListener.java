package org.dromara.hodor.server.listener;

import cn.hutool.core.bean.BeanUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.manager.MetadataManager;
import org.dromara.hodor.server.manager.SchedulerNodeManager;
import org.dromara.hodor.server.service.HodorService;
import org.dromara.hodor.server.service.LeaderService;
import org.dromara.hodor.server.service.RegistryService;

/**
 * server node change listener
 *
 * @author tomgs
 * @since 2020/7/23
 */
@Slf4j
public class SchedulerNodeChangeListener implements DataChangeListener {

    private final SchedulerNodeManager manager;

    private final HodorService hodorService;

    private final LeaderService leaderService;

    private final RegistryService registryService;

    private final MetadataManager metadataManager;

    private final CopySetManager copySetManager;

    private final Map<String, HodorMetadata> pastHodorMetadataMap = new HashMap<>();

    public SchedulerNodeChangeListener(final SchedulerNodeManager schedulerNodeManager, final HodorService hodorService,
                                       final LeaderService leaderService, final RegistryService registryService) {
        this.manager = schedulerNodeManager;
        this.hodorService = hodorService;
        this.leaderService = leaderService;
        this.registryService = registryService;
        this.metadataManager = MetadataManager.getInstance();
        this.copySetManager = CopySetManager.getInstance();
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        /*
         * 1、判断是否已经存在主节点，如果存在说明集群已经启动，进行了任务分配，如果没有主节点，可能主节点挂掉或者集群正在启动，此时不需要进行任务分配，等待选出主节点，由主节点进行任务分配工作。
         * 2、如果主节点下线，那么会重新进行选举进行任务分配，不需要在监听到节点变化的时候去更新元数据。
         * 3、如果非主节点上（下）线，则不会重新进行选举，此时也不需要进行任务的大规模迁移，只需要进行相关节点CopySet的主从切换即可
         * 4、这里采用高可用的方式，在切换的时候为了让任务快速恢复继续执行，则可能会出现元数据的不一致性，此后根据异常检测线程去执行任务的异常检测进行任务补偿
         * 5、在恢复时为了保证任务的继续执行，可能会两个任务同时执行，但是通过在执行检测任务是否正在执行，去保证任务同一时间执行一次
         * 6、如果有主节点说明集群已经启动成功，否则集群为失效状态
         */
        // path /scheduler/nodes/${node_ip}
        String nodePath = event.getPath();
        if (!SchedulerNode.isNodePath(nodePath)) {
            return;
        }

        log.info("ServerNodeChange, eventType: {}, path: {}", event.getType(), nodePath);

        List<String> schedulerNodePath = StringUtils.splitPath(nodePath);
        if (schedulerNodePath.size() != 3) {
            return;
        }

        String nodeIp = schedulerNodePath.get(2);
        HodorMetadata metadata = metadataManager.getMetadata();
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            manager.addNodeServer(nodeIp);
            if (!isMasterNode()) {
                return;
            }
            log.info("scheduler add new server {}.", nodeIp);
            hodorService.createNewHodorMetadata();

            /*List<CopySet> copySet = copySetManager.getCopySet(nodeIp);
            HodorMetadata pastHodorMetadata = pastHodorMetadataMap.get(nodeIp);
            if (CollectionUtil.isEmpty(copySet) && pastHodorMetadata == null) {
                log.info("scheduler add new server {}.", nodeIp);
                hodorService.createNewHodorMetadata();
                *//*
                // 说明是新增的节点，在Replicate和ScatterWidth均为2的情况下，扩容一个节点，其实是拆分最后一个CopySet。
                // 3节点 [[127.0.0.1:8081, 127.0.0.1:8082], [127.0.0.1:8082, 127.0.0.1:8083], [127.0.0.1:8081, 127.0.0.1:8083]]
                // 4节点 [[127.0.0.1:8081, 127.0.0.1:8082], [127.0.0.1:8082, 127.0.0.1:8083], [127.0.0.1:8083, 127.0.0.1:8084], [127.0.0.1:8081, 127.0.0.1:8084]]
                // 这样可以减少其它节点的影响
                List<CopySet> copySets = metadata.getCopySets();
                CopySet lastCopySet = copySets.get(copySets.size() - 1);
                DataInterval dataInterval = lastCopySet.getDataInterval();
                String leader = lastCopySet.getLeader();
                List<String> servers = lastCopySet.getServers();
                if (leader.equals(servers.get(1))) {
                    lastCopySet.setLeader(servers.get(1));
                } else {
                    lastCopySet.setLeader(nodeIp);
                }
                long splitDataInterval = dataInterval.getStartInterval() + (dataInterval.getEndInterval() - dataInterval.getStartInterval()) >> 1;
                lastCopySet.setDataInterval(DataInterval.builder()
                    .startInterval(dataInterval.getStartInterval())
                    .endInterval(splitDataInterval)
                    .build());
                lastCopySet.setServers(Lists.newArrayList(servers.get(1), nodeIp));

                CopySet newCopySet = new CopySet();
                newCopySet.setId(lastCopySet.getId() + 1);
                if (leader.equals(servers.get(0))) {
                    newCopySet.setLeader(servers.get(0));
                } else {
                    lastCopySet.setLeader(nodeIp);
                }
                newCopySet.setDataInterval(DataInterval.builder()
                    .startInterval(splitDataInterval)
                    .endInterval(dataInterval.getEndInterval())
                    .build());
                newCopySet.setServers(Lists.newArrayList(servers.get(0), nodeIp));

                copySets.add(newCopySet);
                metadata.getIntervalOffsets().add(metadata.getIntervalOffsets().size() - 1, splitDataInterval);
                registryService.createMetadata(metadata);*//*
            } else {
                // 说明是下线重新上线的节点，将节点重新切换回来，通知之前的active节点切换为standby状态
                if (pastHodorMetadata != null) {
                    log.info("scheduler server {} recovery.", nodeIp);
                    log.info("metadata: {}", pastHodorMetadata);
                    registryService.createMetadata(pastHodorMetadata);
                }
            }*/
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            manager.removeNodeServer(nodeIp);
            if (!isMasterNode()) {
                return;
            }
            pastHodorMetadataMap.put(nodeIp, BeanUtil.copyProperties(metadata, HodorMetadata.class));
            List<CopySet> changedCopySet = copySetManager.getCopySet(nodeIp);
            changedCopySet.forEach(copySet -> {
                CopySet metadataCopySet = metadata.getCopySets().get(copySet.getId());
                metadataCopySet.getServers().removeIf(server -> server.equals(nodeIp));
                if (copySet.getLeader().equals(nodeIp)) {
                    //String newLeader = copySetManager.selectLeaderCopySet(copySet);
                    metadataCopySet.getServers().stream().findAny().ifPresent(newLeader -> {
                        log.info("new leader: {}", newLeader);
                        metadataCopySet.setLeader(newLeader);
                    });
                }
            });
            // update metadata
            registryService.createMetadata(metadata);
        }

    }

    public boolean isMasterNode() {
        if (!leaderService.hasLeader()) {
            log.info("not exist leader node.");
            return false;
        }
        // 节点下线，由主节点通知进行CopySet的主从切换
        return leaderService.isLeader();
    }

}
