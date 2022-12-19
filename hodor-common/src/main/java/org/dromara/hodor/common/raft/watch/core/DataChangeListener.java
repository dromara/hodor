package org.dromara.hodor.common.raft.watch.core;

/**
 * 数据变化监听器
 */
public interface DataChangeListener {

    void dataChanged(DataChangeEvent event);

}
