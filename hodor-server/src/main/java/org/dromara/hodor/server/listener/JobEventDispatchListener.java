package org.dromara.hodor.server.listener;

import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.server.service.HodorService;

/**
 * JobEventDispatchListener
 *
 * @author tomgs
 * @since 2021/8/9
 */
public class JobEventDispatchListener implements DataChangeListener {

    private final HodorService hodorService;

    public JobEventDispatchListener(final HodorService hodorService) {
        this.hodorService = hodorService;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        String eventPath = event.getPath();

    }

}
