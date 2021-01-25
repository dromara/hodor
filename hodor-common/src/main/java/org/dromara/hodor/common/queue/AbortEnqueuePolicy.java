package org.dromara.hodor.common.queue;

import java.util.Queue;

/**
 * 终止入队列策略
 *
 * @author tomgs
 * @since 2021/1/21
 */
public class AbortEnqueuePolicy<T> implements RejectedEnqueueHandler<T> {

  @Override
  public void rejectedExecution(T e, Queue<T> queue) {
    throw new RuntimeException(String.format("队列已满 [%s]，将终止添加元素: [%s]", queue, e));
  }

}
