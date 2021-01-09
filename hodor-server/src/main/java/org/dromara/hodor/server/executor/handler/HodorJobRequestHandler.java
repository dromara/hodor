package org.dromara.hodor.server.executor.handler;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.core.enums.RequestType;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingRequest;
import org.dromara.hodor.remoting.api.message.RequestBody;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.executor.request.SchedulerRequestBody;
import org.dromara.hodor.server.service.RegisterService;
import org.dromara.hodor.server.service.RemotingClientService;

/**
 * job request executor
 *
 * @author tomgs
 * @since 2020/9/22
 */
@Slf4j
public class HodorJobRequestHandler extends QueueConsumerExecutor<HodorJobExecutionContext> {

  private final RemotingClientService clientService;

  private final RegisterService registerService;

  private HodorJobExecutionContext context;

  public HodorJobRequestHandler() {
    ServiceProvider serviceProvider = ServiceProvider.getInstance();
    this.clientService = serviceProvider.getBean(RemotingClientService.class);
    this.registerService = serviceProvider.getBean(RegisterService.class);
  }

  @Override
  public void run() {
    HodorJobExecutionContext context = getData();
    log.info("hodor job request handler, info {}.", context);
    RemotingRequest<RequestBody> request = getRequestBody(context);
    List<Host> hosts = registerService.getAvailableHosts(context);
    for (int i = hosts.size() - 1; i >= 0; i--) {
      try {
        clientService.sendRequest(hosts.get(i), request);
        break;
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private RemotingRequest<RequestBody> getRequestBody(final HodorJobExecutionContext context) {
    return RemotingRequest.builder()
        .header(getHeader())
        .body(SchedulerRequestBody.fromContext(context))
        .build();
  }

  private Header getHeader() {
    return Header.builder()
        .crcCode(RemotingConst.RPC_CRC_CODE)
        .version(RemotingConst.RPC_VERSION)
        .type(RequestType.JOB_EXEC_REQUEST.getCode())
        .build();
  }

  @Override
  public void setData(HodorJobExecutionContext context) {
    this.context = context;
    super.setData(context);
  }

  @Override
  public String fixName() {
    return context.getJobKey();
  }

}
