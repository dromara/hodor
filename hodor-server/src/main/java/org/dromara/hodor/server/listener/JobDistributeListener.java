package org.dromara.hodor.server.listener;

import java.util.List;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.event.ObjectListener;
import org.dromara.hodor.core.entity.CopySet;
import org.dromara.hodor.core.entity.HodorMetadata;

/**
 * job distribute listener
 *
 * @author tomgs
 * @since 2020/7/30
 */
public class JobDistributeListener implements ObjectListener<HodorMetadata> {

    @Override
    public void onEvent(Event<HodorMetadata> event) {
        final HodorMetadata metadata = event.getValue();
        List<String> nodes = metadata.getNodes();
        List<CopySet> copySets = metadata.getCopySets();
        List<Integer> interval = metadata.getInterval();

    }

}
