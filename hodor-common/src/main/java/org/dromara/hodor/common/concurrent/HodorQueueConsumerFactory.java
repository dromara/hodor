package org.dromara.hodor.common.concurrent;

import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.common.disruptor.QueueConsumerFactory;

/**
 * HodorJobConsumerFactory
 *
 * @author tomgs
 * @since 1.0
 */
@Deprecated
public class HodorQueueConsumerFactory<T> implements QueueConsumerFactory<T> {

  private final String fixName;

  private final QueueConsumerExecutor<T> executor;

  public HodorQueueConsumerFactory(final String fixName) {
    this(fixName, null);
  }

  public HodorQueueConsumerFactory(final String fixName, final QueueConsumerExecutor<T> executor) {
    this.fixName = fixName;
    this.executor = executor;
  }

  @Override
  public QueueConsumerExecutor<T> create() {
    return this.executor;
  }

  @Override
  public String fixName() {
    return fixName;
  }

}
