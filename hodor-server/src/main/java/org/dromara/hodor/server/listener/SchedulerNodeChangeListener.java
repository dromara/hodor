package org.dromara.hodor.server.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.manager.MetadataManager;
import org.dromara.hodor.server.manager.SchedulerNodeManager;
import org.dromara.hodor.server.service.LeaderService;

/**
 * server node change listener
 *
 * @author tomgs
 * @since 2020/7/23
 */
@Slf4j
public class SchedulerNodeChangeListener implements DataChangeListener {

    private final SchedulerNodeManager manager;

    private final LeaderService leaderService;

    public SchedulerNodeChangeListener(final SchedulerNodeManager schedulerNodeManager, final LeaderService leaderService) {
        this.manager = schedulerNodeManager;
        this.leaderService = leaderService;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        // path /scheduler/nodes/${node_ip}
        String nodePath = event.getPath();
        if (!SchedulerNode.isNodePath(nodePath)) {
            return;
        }

        log.info("ServerNodeChange, eventType: {}, path: {}", event.getType(), nodePath);

        /*
         * 1、如果主节点下线，那么会重新进行选举进行任务分配，不需要在监听到节点变化的时候去更新元数据。
         * 2、如果非主节点上（下）线，则不会重新进行选举，此时也不需要进行任务的大规模迁移，只需要进行相关节点CopySet的主从切换即可
         * 3、这里采用高可用的方式，在切换的时候为了让任务继续执行，则可能会出现元数据的不一致性，此后根据异常检测线程去执行任务的异常检测进行任务补偿
         */
        if (!leaderService.hasLeader()) {
            log.info("not exist leader node.");
            return;
        }

        List<String> schedulerNodePath = StringUtils.splitPath(nodePath);
        if (schedulerNodePath.size() != 3) {
            return;
        }

        String nodeIp = schedulerNodePath.get(2);
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            // is master
            manager.addNodeServer(nodeIp);
            //TODO: 更新元数据，判断当前节点是否启动中，还是启动完成
            HodorMetadata metadata = MetadataManager.getInstance().getMetadata();
            CopySetManager.getInstance().getCopySet("");
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            manager.removeNodeServer(nodeIp);
            //TODO: 更新元数据
        }

    }

}
