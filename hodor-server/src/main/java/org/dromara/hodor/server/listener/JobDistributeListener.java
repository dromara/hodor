package org.dromara.hodor.server.listener;

import java.util.List;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.ObjectListener;
import org.dromara.hodor.common.utils.LocalHost;
import org.dromara.hodor.core.entity.CopySet;
import org.dromara.hodor.core.entity.HodorMetadata;
import org.dromara.hodor.core.manager.CopySetManager;

/**
 * job distribute listener
 *
 * @author tomgs
 * @since 2020/7/30
 */
public class JobDistributeListener implements ObjectListener<HodorMetadata> {

    private final CopySetManager copySetManager = CopySetManager.getInstance();

    @Override
    public void onEvent(Event<HodorMetadata> event) {
        final HodorMetadata metadata = event.getValue();
        List<CopySet> copySets = metadata.getCopySets();
        copySets.forEach(e -> {
            if (LocalHost.getIp().equals(e.getLeader())) {
                // 主节点数据区间
                List<Integer> dataInterval = e.getDataInterval();

                // 备用节点数据
                List<String> servers = e.getServers();
                servers.forEach(server -> {
                    CopySet slaveCopySet = copySetManager.getCopySet(server);
                    List<Integer> slaveDataInterval = slaveCopySet.getDataInterval();

                });
            }
        });
    }

}
