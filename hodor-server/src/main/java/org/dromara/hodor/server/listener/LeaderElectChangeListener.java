package org.dromara.hodor.server.listener;

import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.server.service.HodorService;

/**
 * leader elect change listener
 * 
 * @author tomgs
 * @since 2020/7/24
 */
public class LeaderElectChangeListener implements DataChangeListener {

    private final HodorService hodorService;

    public LeaderElectChangeListener(final HodorService hodorService) {
        this.hodorService = hodorService;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            hodorService.electLeader();
        }
    }

}
