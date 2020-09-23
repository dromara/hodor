package org.dromara.hodor.scheduler.api.handler;

import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.register.api.RegisterManager;
import org.dromara.hodor.scheduler.api.RemotingManager;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * job request executor
 *
 * @author tomgs
 * @since 2020/9/22
 */
public class HodorJobRequestHandler extends QueueConsumerExecutor<HodorJobExecutionContext> {

  private final RegisterManager registerManager;

  private final RemotingManager remotingManager;

  public HodorJobRequestHandler() {
    this.registerManager = RegisterManager.getInstance();
    this.remotingManager = RemotingManager.getInstance();
  }

  @Override
  public void run() {
    HodorJobExecutionContext context = getData();
    System.out.println(Thread.currentThread().getName() + "--------------" + context);
  }

}
