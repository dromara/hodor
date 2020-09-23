package org.dromara.hodor.scheduler.api.executor;

import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * job request executor
 *
 * @author tomgs
 * @since 2020/9/22
 */
public class HodorJobRequestExecutor extends QueueConsumerExecutor<HodorJobExecutionContext> {

  @Override
  public void run() {
    HodorJobExecutionContext context = getData();
    System.out.println(Thread.currentThread().getName() + "--------------" + context);
  }

}
