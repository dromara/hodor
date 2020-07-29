package org.dromara.hodor.server.listener;

import org.dromara.hodor.core.manager.MetadataManager;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;

/**
 * metadata change listener
 *
 * @author tomgs
 * @since 2020/7/23
 */
public class MetadataChangeListener implements DataChangeListener {

    private final MetadataManager metadataManager;

    public MetadataChangeListener(final MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {

        String metadata = new String(event.getData());
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            //根据元数据变化，更新节点得信息，任务分配情况
            metadataManager.loadData(metadata);
        }

        if (event.getType() == DataChangeEvent.Type.NODE_UPDATED
            || event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            metadataManager.update(metadata);
        }

    }

}
