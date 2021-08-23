package org.dromara.hodor.server.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
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
 * @since 2020/7/23
 */
@Slf4j
public class MetadataChangeListener extends AbstractAsyncEventPublisher<HodorMetadata> implements DataChangeListener {

    private final HodorService hodorService;

    private final MetadataManager metadataManager;

    private final CopySetManager copySetManager;

    private final GsonUtils gsonUtils;

    public MetadataChangeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
        this.metadataManager = MetadataManager.getInstance();
        this.copySetManager = CopySetManager.getInstance();
        this.gsonUtils = GsonUtils.getInstance();
        this.addListener(new JobInitDistributeListener(hodorService), EventType.JOB_INIT_DISTRIBUTE);
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
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
            List<CopySet> preCopySet = copySetManager.getCopySet(serverEndpoint);
            // 获取当前节点元数据，校验元数据是否变化
            Map<String, Set<CopySet>> parseMetadata = metadataManager.parseMetadata(hodorMetadata);
            Set<CopySet> copySets = parseMetadata.get(serverEndpoint);
            if (CollectionUtil.isEmpty(copySets)) {
                return;
            }
            List<CopySet> changedCopySet = Lists.newArrayList(copySets);
            boolean unChanged = CollectionUtil.isEqualList(preCopySet, changedCopySet);

            metadataManager.loadData(hodorMetadata);
            copySetManager.syncWithMetadata(hodorMetadata);
            if (!unChanged) {
                log.info("Server {} copySet metadata is changed, pre:{}, changed:{}.", serverEndpoint, preCopySet, changedCopySet);
                notifyJobDistribute(metadataManager);
            }
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            log.warn("metadata path {} removed.", event.getPath());
        }
    }

    private void notifyJobDistribute(MetadataManager metadataManager) {
        this.publish(metadataManager.getMetadata(), EventType.JOB_INIT_DISTRIBUTE);
    }

}
