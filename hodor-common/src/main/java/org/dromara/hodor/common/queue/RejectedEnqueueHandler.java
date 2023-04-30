package org.dromara.hodor.common.queue;

import java.util.Queue;

/**
 * 入队拒绝策略
 *
 * @author tomgs
 * @since 1.0
 */
public interface RejectedEnqueueHandler<T> {

  void rejectedExecution(T e, Queue<T> queue);

}
