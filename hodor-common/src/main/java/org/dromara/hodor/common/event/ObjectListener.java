package org.dromara.hodor.common.event;

import java.util.EventListener;

/**
 * 定义事件处理接口
 */
public interface ObjectListener<V> extends EventListener {

    void onEvent(Event<V> event);
}
