package org.dromara.hodor.common.queue;

import java.util.Queue;

/**
 * 扩容队列策略，同ArrayList扩容
 *
 * @author tomgs
 * @since 2021/1/21
 */
public class ResizeQueuePolicy<T> implements RejectedEnqueueHandler<T> {

  @Override
  public void rejectedExecution(T e, Queue<T> queue) {
    CircleQueue<T> loopQueue = (CircleQueue<T>) queue;
    loopQueue.resize(loopQueue.getCapacity() + loopQueue.getCapacity() >> 1);
  }

}
