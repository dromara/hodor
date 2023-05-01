package org.dromara.hodor.common.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.Queue;

/**
 * 丢弃最旧的元素策略
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class DiscardOldestElementPolicy<T> implements RejectedEnqueueHandler<T> {

  @Override
  public void rejectedExecution(T e, Queue<T> queue) {
    Object old = queue.poll();
    log.warn("discard the oldest element: [{}], stack: {}", old, Thread.currentThread().getStackTrace());
  }

}
