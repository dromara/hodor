package org.dromara.hodor.common.event;

import java.util.EventListener;

/**
 * 定义事件处理接口
 * @author tomgs
 */
public interface HodorEventListener<V> extends EventListener {

    /**
     * 事件触发
     *
     * @param event 事件对象
     */
    void onEvent(final Event<V> event);
}
