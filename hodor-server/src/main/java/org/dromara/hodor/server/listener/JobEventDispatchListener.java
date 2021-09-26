package org.dromara.hodor.server.listener;

import java.util.List;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.server.common.EventType;
import org.dromara.hodor.server.service.HodorService;

/**
 * JobEventDispatchListener
 *
 * @author tomgs
 * @since 2021/8/9
 */
public class JobEventDispatchListener extends AbstractAsyncEventPublisher<String> implements DataChangeListener {

    private final HodorService hodorService;

    public JobEventDispatchListener(final HodorService hodorService) {
        this.hodorService = hodorService;
    }

    @Override
    public void registryListener() {
        registerJobCreateListener();
        registerJobUpdateListener();
    }

    private void registerJobCreateListener() {
        this.addListener(event -> {

        }, EventType.JOB_CREATE_DISTRIBUTE);
    }

    private void registerJobUpdateListener() {
        this.addListener(event -> {

        }, EventType.JOB_UPDATE_DISTRIBUTE);
    }

    @Override
    public void dataChanged(DataChangeEvent event) {
        String eventPath = event.getPath();
        // /scheduler/job-event/{eventType}
        List<String> paths = StringUtils.splitPath(eventPath);
        if (paths.size() != 3) {
            return;
        }
        String eventType = paths.get(2);
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED || event.getType() == DataChangeEvent.Type.NODE_UPDATED) {
            publish(Event.create(SerializeUtils.deserialize(event.getData(), String.class), eventType));
        }
    }

}
