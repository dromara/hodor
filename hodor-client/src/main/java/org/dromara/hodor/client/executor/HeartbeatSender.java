package org.dromara.hodor.client.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.config.HeartbeatMsg;

/**
 * 心跳发送服务
 *
 * @author tomgs
 * @since 2021/1/7
 */
@Slf4j
public class HeartbeatSender implements Runnable {

    private final HodorApiClient hodorApiClient = ServiceProvider.getInstance().getBean(HodorApiClient.class);

    @Override
    public void run() {
        try {
            HeartbeatMsg msg = new HeartbeatMsg();
            hodorApiClient.sendHeartbeat(msg);
        } catch (Exception e) {
            log.warn("HeartbeatSender send message has exception, msg: {}", e.getMessage(), e);
        }
    }

}
