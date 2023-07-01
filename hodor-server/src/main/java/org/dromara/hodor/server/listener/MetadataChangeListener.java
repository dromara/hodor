package org.dromara.hodor.server.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.common.EventType;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.manager.MetadataManager;
import org.dromara.hodor.server.service.HodorService;

/**
 * metadata change listener
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class MetadataChangeListener extends AbstractAsyncEventPublisher<List<CopySet>> implements DataChangeListener {

    private final HodorService hodorService;

    private final MetadataManager metadataManager;

    private final CopySetManager copySetManager;

    private final GsonUtils gsonUtils;

    public MetadataChangeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
        this.metadataManager = MetadataManager.getInstance();
        this.copySetManager = CopySetManager.getInstance();
        this.gsonUtils = GsonUtils.getInstance();
        HodorSchedulerChangeListener hodorSchedulerChangeListener = new HodorSchedulerChangeListener(hodorService);
        this.addListener(hodorSchedulerChangeListener, EventType.SCHEDULER_DELETE);
        this.addListener(hodorSchedulerChangeListener, EventType.SCHEDULER_UPDATE);
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        log.info("Metadata changed, event: {}", event);
        if (!SchedulerNode.isMetadataPath(event.getPath())) {
            return;
        }
        final String metadata = new String(event.getData());
        final HodorMetadata hodorMetadata = gsonUtils.fromJson(metadata, HodorMetadata.class);
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED
            || event.getType() == DataChangeEvent.Type.NODE_UPDATED) {
            //根据元数据变化，更新节点得信息，任务分配情况
            if (metadataManager.isEqual(metadataManager.getMetadata(), hodorMetadata)) {
                return;
            }
            // 只针对当前节点元数据变化的判断，所以如果当前节点元数据有变化则进行相应的更新，这样可以在节点重新上下线时
            // 避免集群整体的震荡
            String serverEndpoint = hodorService.getServerEndpoint();
            List<CopySet> historyCopySets = copySetManager.getCopySet(serverEndpoint);
            // 获取当前节点元数据，校验元数据是否变化
            List<CopySet> changedCopySets = ListUtil.toList(metadataManager.parseMetadata(hodorMetadata).get(serverEndpoint));
            boolean unChanged = CollectionUtil.isEqualList(historyCopySets, changedCopySets);
            // 其他节点可能会有变化，所以这里还是需要进行数据的同步处理
            metadataManager.loadData(hodorMetadata);
            copySetManager.syncWithMetadata(hodorMetadata);
            if (!unChanged) {
                log.info("Server {} copySet metadata is changed, pre:{}, changed:{}.", serverEndpoint, historyCopySets, changedCopySets);
                notifyDispatchJob(changedCopySets);
                notifyPurgeHistoryScheduler(getPurgeCopySets(historyCopySets, changedCopySets));
            }
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            log.warn("metadata path {} removed.", event.getPath());
        }
    }

    private List<CopySet> getPurgeCopySets(List<CopySet> historyCopySets, List<CopySet> changedCopySets) {
        if (CollectionUtil.isEmpty(historyCopySets) || CollectionUtil.isEmpty(changedCopySets)
            || historyCopySets.size() <= changedCopySets.size()) {
            return CollectionUtil.empty(List.class);
        }
        historyCopySets.sort(Comparator.comparingInt(CopySet::getId));
        changedCopySets.sort(Comparator.comparingInt(CopySet::getId));
        List<CopySet> purgeCopySets = CollectionUtil.newArrayList();
        for (int i = changedCopySets.size(); i < historyCopySets.size(); i++) {
            purgeCopySets.add(historyCopySets.get(i));
        }
        return purgeCopySets;
    }

    private void notifyPurgeHistoryScheduler(List<CopySet> purgeCopySets) {
        if (CollectionUtil.isEmpty(purgeCopySets)) {
            return;
        }
        this.publish(Event.create(purgeCopySets, EventType.SCHEDULER_DELETE));
    }

    private void notifyDispatchJob(List<CopySet> changedCopySets) {
        this.publish(Event.create(changedCopySets, EventType.SCHEDULER_UPDATE));
    }

}
