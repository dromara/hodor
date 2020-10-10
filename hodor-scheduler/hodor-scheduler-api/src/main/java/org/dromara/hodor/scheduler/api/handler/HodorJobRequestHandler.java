package org.dromara.hodor.scheduler.api.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.core.Host;
import org.dromara.hodor.core.enums.RequestType;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingRequest;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.scheduler.api.RegisterManager;
import org.dromara.hodor.scheduler.api.RemotingManager;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.scheduler.api.common.SchedulerRequestBody;

/**
 * job request executor
 *
 * @author tomgs
 * @since 2020/9/22
 */
@Slf4j
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
    log.info("hodor job request handler, info {}.", context);
    RemotingRequest<RequestBody> request = RemotingRequest.builder().header(getHeader()).body(SchedulerRequestBody.fromContext(context)).build();
    Host host = registerManager.selectSuitableHost(context);
    remotingManager.sendRequest(host, request);
  }

  private Header getHeader() {
    return Header.builder().crcCode(RemotingConst.RPC_CRC_CODE).version(RemotingConst.RPC_VERSION).type(RequestType.JOB_EXEC_REQUEST.getCode()).build();
  }

}
