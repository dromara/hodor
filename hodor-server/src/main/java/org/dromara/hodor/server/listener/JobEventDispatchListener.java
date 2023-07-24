package org.dromara.hodor.server.listener;

import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.server.common.EventType;
import org.dromara.hodor.server.service.HodorService;

/**
 * JobEventDispatchListener
 *
 * @author tomgs
 * @since 1.0
 */
public class JobEventDispatchListener extends AbstractAsyncEventPublisher<Object> {

    private final HodorService hodorService;

    public JobEventDispatchListener(final HodorService hodorService) {
        this.hodorService = hodorService;
    }

    @Override
    public void registryListener() {
        registerJobCreateListener();
        registerJobUpdateListener();
        registerJobDeleteListener();
        registerJobExecuteListener();
    }

    private void registerJobCreateListener() {
        this.addListener(event -> {

        }, EventType.JOB_CREATE_DISTRIBUTE);
    }

    private void registerJobUpdateListener() {
        this.addListener(event -> {

        }, EventType.JOB_UPDATE_DISTRIBUTE);
    }

    private void registerJobDeleteListener() {
        this.addListener(event -> {

        }, EventType.JOB_DELETE_DISTRIBUTE);
    }

    private void registerJobExecuteListener() {
        this.addListener(event -> {

        }, EventType.JOB_DELETE_DISTRIBUTE);
    }

}
