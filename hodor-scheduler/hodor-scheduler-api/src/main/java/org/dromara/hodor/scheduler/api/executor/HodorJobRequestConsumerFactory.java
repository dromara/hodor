package org.dromara.hodor.scheduler.api.executor;

import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.common.disruptor.QueueConsumerFactory;

/**
 * HodorJobConsumerFactory
 *
 * @author tomgs
 * @since 2020/9/22
 */
public class HodorJobRequestConsumerFactory<T> implements QueueConsumerFactory<T> {

  private final String jobKey;
  private final QueueConsumerExecutor<T> executor;

  public HodorJobRequestConsumerFactory(final String jobKey, final QueueConsumerExecutor<T> executor) {
    this.jobKey = jobKey;
    this.executor = executor;
  }

  @Override
  public QueueConsumerExecutor<T> create() {
    return executor;
  }

  @Override
  public String fixName() {
    return jobKey;
  }

}
