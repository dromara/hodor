package org.dromara.hodor.scheduler.api.executor;

import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.common.disruptor.QueueConsumerFactory;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * HodorJobConsumerFactory
 *
 * @author tomgs
 * @since 2020/9/22
 */
public class HodorJobRequestConsumerFactory implements QueueConsumerFactory<HodorJobExecutionContext> {

  private final String jobKey;
  private final QueueConsumerExecutor<HodorJobExecutionContext> executor;

  public HodorJobRequestConsumerFactory(final String jobKey, final QueueConsumerExecutor<HodorJobExecutionContext> executor) {
    this.jobKey = jobKey;
    this.executor = executor;
  }

  @Override
  public QueueConsumerExecutor<HodorJobExecutionContext> create() {
    return executor;
  }

  @Override
  public String fixName() {
    return jobKey;
  }

}
