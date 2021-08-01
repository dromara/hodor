package org.dromara.hodor.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.server.manager.MetadataManager;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.node.ServerNode;
import org.dromara.hodor.server.component.EventType;
import org.dromara.hodor.server.service.HodorService;

/**
 * metadata change listener
 *
 * @author tomgs
 * @since 2020/7/23
 */
@Slf4j
public class MetadataChangeListener extends AbstractEventPublisher<HodorMetadata> implements DataChangeListener {

    private final MetadataManager metadataManager;

    private final GsonUtils gsonUtils;

    public MetadataChangeListener(final HodorService hodorService) {
        this.metadataManager = MetadataManager.getInstance();
        this.gsonUtils = GsonUtils.getInstance();
        this.addListener(new JobDistributeListener(hodorService), EventType.JOB_DISTRIBUTE);
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        if (!ServerNode.isMetadataPath(event.getPath())) {
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
            metadataManager.loadData(hodorMetadata);
            notifyJobDistribute(metadataManager);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            log.warn("metadata path {} removed.", event.getPath());
        }

    }

    private void notifyJobDistribute(MetadataManager metadataManager) {
        this.publish(metadataManager.getMetadata(), EventType.JOB_DISTRIBUTE);
    }

}
